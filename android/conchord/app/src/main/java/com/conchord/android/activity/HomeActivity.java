package com.conchord.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.conchord.android.R;
import com.conchord.android.util.Constants;
import com.conchord.android.util.L;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;

/*
 * TODO: I shouldn't force the vertical orientation in the app manifest but 
 * should instead put all the data i want to persist through orientation 
 * changes into a Fragment: http://goo.gl/X53ni0, http://goo.gl/VA7GH
 */
public class HomeActivity extends Activity {

	private static final String TAG = HomeActivity.class.getSimpleName();
	private SharedPreferences prefs;

	private Button buttonCreateSession;
	private EditText editTextSessionName;

	private Button buttonJoinSession;
	private EditText editTextJoinSessionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_home);

		this.prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		setupGUI();

		// Check if we have a user ID for the device **Isn't unique yet**
		if (!userIdExists()) {
			storeUserID(android.os.Build.MODEL);
		} else {
            L.d(TAG, "User id exists: \"" + prefs.getString(Constants.KEY_HOST_ID, "") + "\".");
        }
	}

	private void setupGUI() {
		buttonCreateSession = (Button) findViewById(R.id.buttonCreateSession);
		editTextSessionName = (EditText) findViewById(R.id.editTextSessionName);

		buttonCreateSession.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final String sessionName = editTextSessionName.getText().toString();

				// Make sure session to create has valid length
				if (sessionName.length() > 0) {

					// Check to see if the session is unique.
					final Firebase firebaseCreatedSession = new Firebase(Constants.sessionsUrl
							+ sessionName);

					firebaseCreatedSession.addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot arg0) {
							Object value = arg0.getValue();

							if (value == null) {
//								editTextSessionName.setText("");

                                // Because we're starting the activity, stop
                                // listening.
                                firebaseCreatedSession.removeEventListener(this);

								Intent intent = new Intent(getApplicationContext(),
										SessionActivity.class);
								intent.putExtra(Constants.KEY_SESSION, sessionName);
								intent.putExtra(Constants.KEY_IS_HOST, true);
								// intent.putExtra(Constants.KEY_CREATOR, true);
								startActivity(intent);

							} else {
								Toast.makeText(getBaseContext(),
										"session is not unique", Toast.LENGTH_SHORT)
										.show();
								firebaseCreatedSession.removeEventListener(this);
							}
						}

						@Override
						public void onCancelled() {

						}
					});

					buttonCreateSession.setEnabled(false);

				} else {
					// toast saying no length
					Toast.makeText(getBaseContext(),
							"Give a valid length session name", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});

		editTextJoinSessionName = (EditText) findViewById(R.id.editTextJoinSessionName);
		buttonJoinSession = (Button) findViewById(R.id.buttonJoinSession);
		
		buttonJoinSession.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String sessionName = editTextJoinSessionName.getText()
						.toString();

				// Make sure session to create has valid length
				if (sessionName.length() > 0) {

					// Create the Firebase
					final Firebase firebaseJoinedSession = new Firebase(Constants.sessionsUrl
							+ sessionName);

					firebaseJoinedSession.addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot arg0) {
							Object value = arg0.getValue();

							if (value != null) {
								
								L.e(TAG, arg0.child(Constants.KEY_SESSION_CLOSED)
										.getValue().toString());
								
								if (!arg0.child(Constants.KEY_SESSION_CLOSED)
										.getValue().equals(false)) {
									Toast.makeText(getApplicationContext(),
											"This session is closed.", Toast.LENGTH_SHORT)
											.show();
									firebaseJoinedSession.removeEventListener(this);
									return;
								}
//								editTextJoinSessionName.setText("");

								// Join jam session
								Intent intent = new Intent(getApplicationContext(),
										SessionActivity.class);
								intent.putExtra(Constants.KEY_SESSION, sessionName);
								intent.putExtra(Constants.KEY_IS_HOST, false);
								// intent.putExtra(Constants.KEY_CREATOR, false);
								startActivity(intent);
								firebaseJoinedSession.removeEventListener(this);
							} else {
								Toast.makeText(getApplicationContext(),
										"Can't find this session", Toast.LENGTH_SHORT)
										.show();
								firebaseJoinedSession.removeEventListener(this);
							}
						}

						@Override
						public void onCancelled() {	}
					});
				} else {
					Toast.makeText(getApplicationContext(),
							"Enter something first!", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}

	private void storeUserID(String userId) {
		SharedPreferences.Editor prefsEditor = prefs.edit();
		prefsEditor.putString(Constants.KEY_HOST_ID, userId);
		prefsEditor.commit();
        L.d(TAG, "Storing user id \"" + userId + "\" in prefs.");
	}

	private boolean userIdExists() {
		return (prefs.getString(Constants.KEY_HOST_ID, null) == null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		buttonCreateSession.setEnabled(true);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
