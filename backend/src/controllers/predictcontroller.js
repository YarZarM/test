import axios from 'axios';
import { supabase } from '../config/supabase.js';
import dotenv from 'dotenv';
import admin from '../config/firebase.js';
dotenv.config();
import { GoogleGenAI } from "@google/genai";

// ✅ create the Gemini client here, in module scope
const ai = new GoogleGenAI({
  apiKey: process.env.GEMINI_API_KEY,   // or omit and it will read from env automatically
});

// choose the model you want
const MODEL_NAME = "gemini-2.5-flash";  // or "gemini-1.5-pro" etc.

const RECOMMENDATIONS = {
    stress: [
        "10-min deep breathing or guided meditation",
        "Take 15-min short breaks during work",
        "15-min light physical activity like walking, yoga or stretching"
    ],
    workload: [
        "Break tasks into smaller chunks and focus on one at a time",
        "Set realistic goals for the day and prioritize high-impact tasks",
        "Take 5-min break after completing each major task"
    ],
    hrv: [
        "Drink 1 glass of water every hour to stay hydrated",
        "Maintain a consistent sleep schedule of 7-9 hours per night",
        "Include light movement or relaxation before bedtime"
    ]
};

const getRiskRecommendation = (p_next_hour) => {
    if(p_next_hour >= 0.7) {
        return "High migraine risk today. Consider taking preventive measures such as resting, staying hydrated, and avoiding known triggers.";
    } else if (p_next_hour >= 0.4) {
        return "Moderate migraine risk today. Stay alert to your symptoms, take short breaks and consider light activities that help reduce stress.";
    } else {
        return "Low migraine risk today. Maintain your current routine and continue monitoring your metrics.";
    }
}

const getRecommendedActions = (top_factors) => {
    const recommended_actions = [];
    if (Array.isArray(top_factors) && top_factors.length > 0) {
        top_factors.forEach(factor => {
            const { feature, direction } = factor;
            if (direction === "risk_up" && RECOMMENDATIONS[feature]) {
                recommended_actions.push(...RECOMMENDATIONS[feature]);
            }
        });
    }

    if (recommended_actions.length === 0) {
        recommended_actions.push("No high-risk indicators detected. Keep maintaining a healthy lifestyle and continue monitoring your metrics.");
    }
    return recommended_actions;
}

const getGeminiRecommendations = async (p_next_hour, top_factors) => {
  try {
    const prompt = `
You are a helpful assistant that suggests non-medical, lifestyle-based actions 
to help a migraine-prone office worker manage their risk.

You are given:
- Next-hour migraine probability (0–1).
- A list of top contributing factors with their scores between 0 and 1.

Only give general wellbeing advice (rest, hydration, breaks, relaxation, ergonomics, light exercise, etc.).
Do NOT mention medication, diagnosis, or urgent medical instructions.
Keep each action short and concrete.

Return a pure JSON object with this exact shape and nothing else:

{
  "summary": "one-sentence summary in plain English",
  "actions": [
    "first recommended action",
    "second recommended action",
    "third recommended action"
  ]
}

Here is the input:

- p_next_hour: ${p_next_hour}
- top_factors: ${JSON.stringify(top_factors, null, 2)}
    `;

    const response = await ai.models.generateContent({
      model: MODEL_NAME,
      contents: [
        {
          role: "user",
          parts: [{ text: prompt }]
        }
      ]
    });

    const text = response.text ?? "";
    console.log("Gemini raw response:", text);

    // Grab the JSON part between first { and last }
    const start = text.indexOf("{");
    const end = text.lastIndexOf("}");
    if (start === -1 || end === -1 || end <= start) {
      throw new Error("No JSON object found in Gemini response");
    }

    const jsonSnippet = text.slice(start, end + 1);

    let parsed;
    try {
      parsed = JSON.parse(jsonSnippet);
    } catch (e) {
      console.error("JSON parse failed on:", jsonSnippet);
      throw e;
    }

    if (!parsed || !Array.isArray(parsed.actions) || parsed.actions.length === 0) {
      throw new Error("Gemini response missing actions");
    }

    return {
      summary: parsed.summary || "",
      actions: parsed.actions
    };
  } catch (err) {
    console.error("Gemini recommendation error:", err.message);
    return null; // fallback to static rules
  }
};


let slidingCounter = 0; // tracks how many times the function has run
const MAX_RUNS = 2;
const WINDOW_SIZE = 24;

export const runPredictionInternal = async () => {
    const userId = 'YZMM';

    // Stop after 10 runs
    if (slidingCounter >= MAX_RUNS) {
        console.log(`Reached max ${MAX_RUNS} runs. No more predictions.`);
        return;
    }

    // Get ALL rows sorted by timestamp ASCENDING
    const { data: featureData, error: featureError } = await supabase
        .from('features')
        .select('timestamp, workload, stress, hrv')
        .eq('user_id', userId)
        .order('timestamp', { ascending: true });

    if (featureError) throw featureError;

    if (!featureData || featureData.length < WINDOW_SIZE) {
        throw new Error(`Need at least ${WINDOW_SIZE} rows, found ${featureData?.length || 0}`);
    }

    // Calculate start and end
    // Run 1 → start=0
    // Run 2 → start=1
    // Run 3 → start=2
    const start = slidingCounter;
    const end = start + WINDOW_SIZE;

    if (end > featureData.length) {
        console.log(`Not enough rows to continue shifting window. Stopping.`);
        return;
    }

    console.log(`Sliding window run ${slidingCounter + 1}/${MAX_RUNS}`);
    console.log(`Using rows ${start} → ${end - 1}`);

    const windowData = featureData.slice(start, end).map(item => ({
        workload_0_10: item.workload,
        stress_0_10: item.stress,
        hrv_rmssd_ms: item.hrv
    }));

    // increment for next run
    slidingCounter++;

    const mlPayload = {
        window: windowData,
        timestamp: Date.now()
    };

    console.log(`Calling ML service at: ${process.env.ML_URL}`);

    let mlResponse;
    console.log("Raw ML response:", mlResponse);

    let retries = 2;

    for (let i = 0; i <= retries; i++) {
        try {
            mlResponse = await axios.post(process.env.ML_URL, mlPayload, {
                timeout: 60000,
                headers: { 'Content-Type': 'application/json' }
            });
            break;
        } catch (error) {
            console.error(`ML call failed attempt ${i + 1}: ${error.message}`);
            if (i === retries) throw error;
            await new Promise(res => setTimeout(res, 5000));
        }
    }

    const { p_next_hour, top_factors, timestamp } = mlResponse.data;

    console.log(`ML prediction received: p_next_hour=${p_next_hour}`);

    const { error: insertError } = await supabase
        .from('predictions')
        .insert([{ user_id: userId, p_next_hour, top_factors, timestamp }]);

    if (insertError) throw insertError;

    console.log(`Prediction saved.`);

    return {
        p_next_hour,
        top_factors,
        timestamp,
        window_start: start,
        window_end: end - 1,
        run_number: slidingCounter
    };
};

export const runPrediction = async (req, res) => {
    const userId = 'YZMM';
    const features = ['workload', 'stress', 'hrv'];
    const RISK_THRESHOLD = 0.5;
    const DEBOUNCE_HOURS = 0;

    try {
        const { data: featureData, error: featureError } = await supabase
            .from('features')
            .select('timestamp, workload, stress, hrv')
            .eq('user_id', userId)
            .order('timestamp', { ascending: false })
            .limit(24);

        if (featureError) {
            console.error('Supabase select error:', featureError);
            return res.status(500).json({ error: 'Supabase select failed', details: featureError });
        }

        if (!featureData) {
            return res.status(404).json({ error: 'Not enough feature data found for the user.' });
        }

        const data_series = featureData.reverse();

        const windowData = featureData.map(item => {
            return {
                workload_0_10: item.workload,
                stress_0_10: item.stress,
                hrv_rmssd_ms: item.hrv
            };
        })


        const mlPayload = {
            window: windowData,
            timestamp: Date.now()
        }

        const mlResponse = await axios.post(process.env.ML_URL, mlPayload);

        const {p_next_hour, top_factors, timestamp} = mlResponse.data;

        const { error: insertError } = await supabase
            .from('predictions')
            .insert([
                {
                    user_id: userId,
                    p_next_hour,
                    top_factors,
                    timestamp
                }
            ]);

        if (insertError) throw insertError;

        // Commented out the noti function since it will throw error without fcm token and need front end to request fcm token

        // Get user's FCM token and last_notified_at
        const { data: userData, error: userError } = await supabase
            .from('users')
            .select('fcm_token, last_notified_at')
            .eq('id', userId)
            .single();
        if (userError) throw userError;

        const fcmToken = userData?.fcm_token || null;

        let lastNotified;
        if (userData && userData.last_notified_at) {
        lastNotified = new Date(userData.last_notified_at);
        } else {
        lastNotified = new Date(0); // default value
        }
        const hoursSince = (Date.now() - lastNotified.getTime()) / (1000 * 60 * 60);

        // Send notification if threshold met and debounce passed
        if (fcmToken && p_next_hour >= RISK_THRESHOLD && hoursSince >= DEBOUNCE_HOURS) {
            await admin.messaging().send({
                token: fcmToken,
                notification: {
                    title: '⚠️ Migraine Risk Alert',
                    body: `Next-hour migraine risk: ${(p_next_hour * 100).toFixed(0)}%`
                },
                data: {
                    title: '⚠️ Migraine Risk Alert',
                    body: `Next-hour migraine risk: ${(p_next_hour * 100).toFixed(0)}%`
                }
        });

        // Update last_notified_at in DB
        await supabase
            .from('users')
            .update({ last_notified_at: new Date().toISOString() })
            .eq('id', userId);

        console.log('Notification sent and timestamp updated');
        } else {
        console.log('No notification (below threshold or debounce active)');
        }

        // (Optional) map feature names to nicer labels before sending to Gemini
        const featureLabelMap = {
        stress_0_10: "stress",
        workload_0_10: "workload",
        hrv_rmssd_ms: "low heart rate variability (HRV)"
        };

        const mappedTopFactors = top_factors.map(f => ({
        feature: featureLabelMap[f.feature] || f.feature,
        score: f.score
        }));

        // Ask Gemini for recommendations (only if risk is not trivial, to save tokens)
        let geminiResult = null;
        if (p_next_hour >= 0.2) {
        geminiResult = await getGeminiRecommendations(p_next_hour, mappedTopFactors);
        }

        // Fallback to your existing static logic if Gemini fails
        let recommendationSummary;
        let recommendedActions;

        if (geminiResult) {
        recommendationSummary = geminiResult.summary || getRiskRecommendation(p_next_hour);
        recommendedActions = geminiResult.actions;
        } else {
        recommendationSummary = getRiskRecommendation(p_next_hour);
        recommendedActions = getRecommendedActions(top_factors);
        }

        res.json({
        user_id: userId,
        p_next_hour,
        recommendation: recommendationSummary,
        top_factors: top_factors,
        recommended_actions: recommendedActions,
        generated_at: timestamp
        });

        
        // res.json({
        //     user_id: userId,
        //     p_next_hour,
        //     recommendation: getRiskRecommendation(p_next_hour),
        //     top_factors,
        //     recommended_actions: getRecommendedActions(top_factors),
        //     generated_at: timestamp
        // });
    } catch (error) {
        console.error('Error running prediction:', error);
        res.status(500).json({ error: 'Internal server error', details: error.message });
    }
}

export const getLatestPrediction = async (req, res) => {
    const userId = 'YZMM';

    try {
        const { data: predictionData, error: predictionError } = await supabase
            .from('predictions')
            .select('p_next_hour, top_factors, timestamp')
            .eq('user_id', userId)
            .order('timestamp', { ascending: false })
            .limit(1);

        if (predictionError) {
            console.error('Supabase select error:', predictionError);
            return res.status(500).json({ error: 'Supabase select failed', details: predictionError });
        }

        if (!predictionData) {
            return res.status(404).json({ error: 'No prediction found for the user.' });
        }

        const latest = predictionData[0];

        res.json({
            ...latest,
            recommendation: getRiskRecommendation(latest.p_next_hour),
            recommended_actions: getRecommendedActions(latest.top_factors)
        })

    } catch (error) {
        console.error('Error fetching latest prediction:', error);
        res.status(500).json({ error: 'Internal server error', details: error.message });
    }
}

console.log('Loaded predictcontroller.js');
