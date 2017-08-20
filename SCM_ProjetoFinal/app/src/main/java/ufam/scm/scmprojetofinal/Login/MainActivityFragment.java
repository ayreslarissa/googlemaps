package ufam.scm.scmprojetofinal.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.util.Arrays;

import ufam.scm.scmprojetofinal.Cadastro.CadastroFBActivity;
import ufam.scm.scmprojetofinal.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private TextView textView;
    private CallbackManager mCallbackManager;
    private Profile profile;
    private AccessTokenTracker tokenTracker;
    private ProfileTracker profileTracker;
    private  Intent intent;
    private String mensagem;


    public MainActivityFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);




        mCallbackManager = CallbackManager.Factory.create();


        tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken1) {

            }
        };
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile1) {
                textView.setText(displayMessage(profile1));
            }
        };

        tokenTracker.startTracking();
        profileTracker.startTracking();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView = (TextView) view.findViewById(R.id.textView);
        textView.setText("Você não está logado");

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setCompoundDrawables(null, null, null, null);
        loginButton.setReadPermissions(Arrays.asList("user_friends", "email"));
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mFacebookCallback);
    }

    private FacebookCallback<LoginResult> mFacebookCallback = new FacebookCallback<LoginResult>() {

        @Override
        public void onSuccess(LoginResult loginResult) {

            AccessToken accessToken = loginResult.getAccessToken();
            profile = Profile.getCurrentProfile();
            textView.setText(displayMessage(profile));

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                                // Application code
                                Bundle parameters = new Bundle();

                                mensagem = response.toString();

                                parameters.putString("infos", mensagem);


                                intent = new Intent(getActivity(), CadastroFBActivity.class);
                                intent.putExtras(parameters);
                                startActivity(intent);
                                Log.v("LoginActivity", response.toString());
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "name, email");

            request.setParameters(parameters);

            request.executeAsync();

        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException e) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        textView.setText(displayMessage(profile));
    }

    @Override
    public void onStop() {
        super.onStop();
        profileTracker.stopTracking();
        tokenTracker.stopTracking();
    }

    private String displayMessage(Profile profile) {
        StringBuilder stringBuilder = new StringBuilder();
        if (profile != null) {
            Log.d("LOGIN", "logou");
            stringBuilder.append("Logado como " + profile.getFirstName());
            Toast.makeText(getActivity(), "Start Playing with the data " + profile.getFirstName(), Toast.LENGTH_SHORT).show();
        } else {
            Log.d("LOGIN", "nao logou");
            stringBuilder.append("You are not logged in");
        }
        return stringBuilder.toString();
    }

}
