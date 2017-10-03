package com.softbankrobotics.qisdktutorials.ui.tutorials.listen;

import android.os.Bundle;
import android.util.Log;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.ListenBuilder;
import com.aldebaran.qi.sdk.builder.PhraseSetBuilder;
import com.aldebaran.qi.sdk.builder.SayBuilder;
import com.aldebaran.qi.sdk.object.conversation.Listen;
import com.aldebaran.qi.sdk.object.conversation.ListenResult;
import com.aldebaran.qi.sdk.object.conversation.PhraseSet;
import com.aldebaran.qi.sdk.object.conversation.Say;
import com.aldebaran.qi.sdk.util.PhraseSetUtil;
import com.softbankrobotics.qisdktutorials.R;
import com.softbankrobotics.qisdktutorials.ui.conversation.ConversationItemType;
import com.softbankrobotics.qisdktutorials.ui.conversation.ConversationView;
import com.softbankrobotics.qisdktutorials.ui.tutorials.TutorialActivity;

/**
 * The activity for the Listen tutorial.
 */
public class ListenTutorialActivity extends TutorialActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "ListenTutorialActivity";

    private ConversationView conversationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        conversationView = findViewById(R.id.conversationView);

        // Register the RobotLifecycleCallbacks to this Activity.
        QiSDK.register(this, this);
    }

    @Override
    protected void onDestroy() {
        // Unregister all the RobotLifecycleCallbacks for this Activity.
        QiSDK.unregister(this);
        super.onDestroy();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.conversation_layout;
    }

    @Override
    public void onRobotFocusGained(QiContext qiContext) {
        String textToSay = "I can listen to you: say \"Yes\" or \"No\" to try.";
        displayLine(textToSay, ConversationItemType.ROBOT_OUTPUT);

        Say say = SayBuilder.with(qiContext)
                .withText(textToSay)
                .build();

        say.run();

        // Create the PhraseSet 1.
        PhraseSet phraseSetYes = PhraseSetBuilder.with(qiContext) // Create the builder using the QiContext.
                .withTexts("yes", "OK", "alright", "let's do this") // Add the phrases Pepper will listen to.
                .build(); // Build the PhraseSet.

        // Create the PhraseSet 2.
        PhraseSet phraseSetNo = PhraseSetBuilder.with(qiContext) // Create the builder using the QiContext.
                .withTexts("no", "Sorry", "I can't") // Add the phrases Pepper will listen to.
                .build(); // Build the PhraseSet.

        // Create a new listen action.
        Listen listen = ListenBuilder.with(qiContext) // Create the builder with the QiContext.
                .withPhraseSet(phraseSetYes, phraseSetNo) // Set the PhraseSets to listen to.
                .build(); // Build the listen action.

        // Run the listen action and get the result.
        ListenResult listenResult = listen.run();

        String humanText = listenResult.getHeardPhrase().getText();
        String message = "Heard phrase: " + humanText;
        Log.i(TAG, message);
        displayLine(humanText, ConversationItemType.HUMAN_INPUT);

        // Identify the matched phrase set.
        PhraseSet matchedPhraseSet = listenResult.getMatchedPhraseSet();
        if (PhraseSetUtil.equals(matchedPhraseSet, phraseSetYes)) {
            String msg = "Heard phrase set: yes";
            Log.i(TAG, msg);
            displayLine(msg, ConversationItemType.INFO_LOG);
        } else if (PhraseSetUtil.equals(matchedPhraseSet, phraseSetNo)) {
            String msg = "Heard phrase set: no";
            Log.i(TAG, msg);
            displayLine(msg, ConversationItemType.INFO_LOG);
        }
    }

    @Override
    public void onRobotFocusLost() {
        // Nothing here.
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // Nothing here.
    }

    private void displayLine(final String text, final ConversationItemType type) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                conversationView.addLine(text, type);
            }
        });
    }
}
