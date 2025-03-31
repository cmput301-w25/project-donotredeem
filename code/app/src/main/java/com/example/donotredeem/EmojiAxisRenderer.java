    package com.example.donotredeem;
    import android.content.Context;
    import android.graphics.Canvas;
    import android.graphics.drawable.Drawable;

    import androidx.core.content.ContextCompat;

    import com.github.mikephil.charting.components.XAxis;
    import com.github.mikephil.charting.renderer.XAxisRenderer;
    import com.github.mikephil.charting.utils.Transformer;
    import com.github.mikephil.charting.utils.ViewPortHandler;

    /**
     * Custom XAxisRenderer that replaces traditional axis labels with emoji drawables.
     * Enables visualization of emotional states or categories using emoji icons in MPAndroidChart.
     */
    public class EmojiAxisRenderer extends XAxisRenderer {
        private final int[] emojiResources;
        private final Context context;
        private final float emojiSize;
        private final float emojiMargin;

        /**
         * Constructs a custom emoji axis renderer
         * @param viewPortHandler Chart's viewport handler
         * @param xAxis XAxis configuration object
         * @param trans Transformer for coordinate conversions
         * @param context Context for resource access
         * @param emojiResources Array of drawable resources for emojis
         */
        public EmojiAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans, Context context, int[] emojiResources) {
            super(viewPortHandler, xAxis, trans);
            this.context = context;
            this.emojiResources = emojiResources;
            this.emojiSize = context.getResources().getDimension(R.dimen.emoji_chart_size);
            this.emojiMargin = context.getResources().getDimension(R.dimen.emoji_chart_margin);
        }

        /**
         * Overrides default label rendering to draw emoji icons instead of text
         * @param c Canvas to draw on
         */
        @Override
        public void renderAxisLabels(Canvas c) {
            if (!mXAxis.isEnabled() || !mXAxis.isDrawLabelsEnabled()) return;

            float[] positions = new float[Math.min(mXAxis.mEntryCount, emojiResources.length) * 2];
            for (int i = 0; i < positions.length / 2; i++) {  // Ensure we stay within bounds
                positions[i * 2] = i;
                positions[i * 2 + 1] = 0;
            }

            mTrans.pointValuesToPixel(positions);

            for (int i = 0; i < positions.length / 2; i++) {  // Again, limit loop to array size
                drawEmoji(c, i, positions[i * 2]);
            }
            super.renderAxisLabels(c);
        }

        /**
         * Draws individual emoji at specified chart position
         * @param c Canvas to draw on
         * @param position Index in emojiResources array
         * @param xPos Calculated x-position in chart coordinates
         */
        private void drawEmoji(Canvas c, int position, float xPos) {
            if (position < 0 || position >= emojiResources.length) return;

            try {
                Drawable emoji = ContextCompat.getDrawable(context, emojiResources[position]);
                if (emoji == null) return;

                // Convert dp dimensions to pixels
                int size = 40;
                int margin = context.getResources().getDimensionPixelSize(R.dimen.emoji_chart_margin);

                // Calculate positions
                float yPos = mViewPortHandler.contentBottom() + margin + 15; // Moves it down
                float xAdjusted = xPos + 10;  // Moves it right

                float halfSize = size / 2f;

                // Set bounds with exact 20dp x 20dp
                emoji.setBounds(
                        (int) (xPos - halfSize),
                        (int) yPos,
                        (int) (xPos + halfSize),
                        (int) (yPos + size)
                );


                emoji.draw(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }