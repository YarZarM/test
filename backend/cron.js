import cron from 'node-cron';
import { runPredictionInternal, getLatestPredictionInternal } from './src/controllers/predictcontroller.js';

cron.schedule('*/10 * * * * *', async () => {
    console.log('--- Running scheduled prediction task ---');
    try {
        const prediction = await runPredictionInternal();
        console.log('Prediction result:', {
            p_next_hour: prediction.p_next_hour,
            top_factors: prediction.top_factors,
            timestamp: prediction.timestamp
        });
    } catch (error) {
        console.error('Error during scheduled prediction:', error.message);
    }
});

cron.schedule('5-59/10 * * * * *', async () => {
    console.log('--- Fetching latest prediction for monitoring ---');
    try {
        const latest = await getLatestPredictionInternal();
        if (!latest) {
            console.log('No latest prediction found.');
        } else {
            console.log('Latest prediction data:', latest);
        }
    } catch (error) {
        console.error('Error fetching latest prediction:', error.message);
    }
});

console.log('Cron jobs initialized: predictions every 10s, latest fetch offset by 5s');