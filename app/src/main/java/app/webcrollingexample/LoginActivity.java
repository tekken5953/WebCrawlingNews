package app.webcrollingexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    //https://velog.io/@jeongminji4490/Android-Google-Login-%EA%B5%AC%ED%98%84

    GoogleSignInOptions gso;
    GoogleSignInClient mGoogleSignInClient;
    ConstraintLayout loginLayout;
    com.google.android.gms.common.SignInButton login;
    GoogleSignInAccount lastLogin;

    @Override
    protected void onStart() {
        super.onStart();
        if (lastLogin != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, "로그인 상태를 유지합니다", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 앱에 필요한 사용자 데이터를 요청하도록 로그인 옵션을 설정한다.
        // DEFAULT_SIGN_IN parameter는 유저의 ID와 기본적인 프로필 정보를 요청하는데 사용된다.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail() // email addresses도 요청함
                .build();

        // 위에서 만든 GoogleSignInOptions을 사용해 GoogleSignInClient 객체를 만듬
        mGoogleSignInClient = GoogleSignIn.getClient(LoginActivity.this, gso);

        lastLogin = GoogleSignIn.getLastSignedInAccount(LoginActivity.this);

        login = findViewById(R.id.googleLoginBtn);
        loginLayout = findViewById(R.id.loginLayout);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityResult.launch(signInIntent);
    }

    ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                    }
                }
            });

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String email = Objects.requireNonNull(account.getEmail()).toLowerCase(Locale.ROOT);
            String displayName = account.getDisplayName();
            String familyName = account.getFamilyName();
            String givenName = account.getGivenName();
            String id = Objects.requireNonNull(account.getId()).toLowerCase(Locale.ROOT);
            String photo = null;
            if (account.getPhotoUrl() != null) {
                photo = account.getPhotoUrl().toString();
            }
            String token = account.getIdToken();
            Log.d("gLogin", "\nId : " + id + "\nDisplayName : " + displayName + "\nFamilyName : " + familyName +
                    "\nGivenName : " + givenName + "\nToken : " + token + "\nEmail : " + email + "\nprofile : " + photo);
            Snackbar.make(LoginActivity.this, loginLayout, "로그인 성공", Snackbar.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}