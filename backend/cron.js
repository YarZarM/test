import cron from 'node-cron';
import { runPredictionInternal } from './src/controllers/predictcontroller.js';

cron.schedule('*/20 * * * * *', async () => {
    console.log('--- Running scheduled prediction task ---');
    try {
        const prediction = await runPredictionInternal();
        console.log('Prediction result:', {
            p_next_hour: prediction.p_next_hour,
            top_factors: prediction.top_factors,
            timestamp: prediction.timestamp
        });
        // const latest = await getLatestPredictionInternal();
        // if (!latest) {
        //     console.log('No latest prediction found.');
        // } else {
        //     console.log('Latest prediction data:', latest);
        // }
    } catch (error) {
        console.error('Error during scheduled prediction:', error.message);
        console.error('Code:', error.code);
        if (error.response) {
            console.error('Response status:', error.response.status);
            console.error('Response data:', error.response.data);
        }
        console.error('Full error:', error);
    }
});

console.log('Cron jobs initialized: predictions every 10s, latest fetch offset by 5s');