package app.crawling_news;

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

import java.util.Locale;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    // 전체 진행 과정https://velog.io/@jeongminji4490/Android-Google-Login-%EA%B5%AC%ED%98%84
    // 구글 콘솔 등록 developers.google.com/identity/sign-in/android/start-integrating

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
            Toast.makeText(this, lastLogin.getDisplayName() + "님 환영합니다!", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("Tag", "Last login Session is NULL");
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
        login.setEnabled(false);
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
                    } else {
                        Toast.makeText(LoginActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        Log.e("Tag", "result code is " + result.getResultCode());
                        login.setEnabled(true);
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
            String token = account.getIdToken();
            if (account.getPhotoUrl() != null) {
                photo = account.getPhotoUrl().toString();
            }

            Log.d("gLogin", "\nId : " + id + "Account" + account + "\nDisplayName : " + displayName
                    + "\nFamilyName : " + familyName + "\nGivenName : " + givenName + "\nToken : "
                    + token + "\nEmail : " + email + "\nprofile : " + photo);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this, displayName + "님 환영합니다!",
                            Toast.LENGTH_SHORT).show();
                }
            });
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}