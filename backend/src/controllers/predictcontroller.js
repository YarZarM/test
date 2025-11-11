import axios from 'axios';
import { supabase } from '../config/supabase.js';
import dotenv from 'dotenv';
dotenv.config();

export const runPrediction = async (req, res) => {
    const userId = 'YZMM';
    const features = ['workload', 'stress', 'hrv'];

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

        const mlPayload = {
            user_id: userId,
            window_size: 24,
            features: features,
            series: data_series
        }

        let mlResponse;

        if (!process.env.ML_URL) {
        // Mock ML response
        console.log("ML_URL not set, using mock prediction");
        mlResponse = {
            data: {
            p_next_hour: Math.random().toFixed(2), // dummy prediction
            top_factors: ['stress', 'workload'],
            timestamp: new Date().toISOString()
            }
        };
        } else {
            mlResponse = await axios.post(process.env.ML_URL, mlPayload);
        }

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
        
        res.json({
            user_id: userId,
            p_next_hour,
            top_factors,
            generated_at: timestamp
        });
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

        res.json(predictionData[0]);
    } catch (error) {
        console.error('Error fetching latest prediction:', error);
        res.status(500).json({ error: 'Internal server error', details: error.message });
    }
}

console.log('Loaded predictcontroller.js');
