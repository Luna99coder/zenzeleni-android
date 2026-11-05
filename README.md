# Zenzeleni

A community-driven loyalty and rewards Android app for the South African market, built in Kotlin for the OPSC6312 Portfolio of Evidence (Final PoE).

## Purpose of the app

Zenzeleni ("do it yourselves / do it together" in isiZulu) lets people earn and spend reward points. The Part 1 research found three gaps in South African loyalty apps: accessibility (no bank account needed), trust, and language. The app responds with email and Google sign-in, an English/isiZulu interface, challenges that work offline, and a simple points-and-rewards flow.

## Design considerations

- **Simple screens:** Login, Register, Home, Challenges, Rewards and Settings, each with one clear job, for users with different levels of digital literacy.
- **Two languages:** all interface text lives in string resources (`values/` and `values-zu/`), so Android swaps languages without restarting the app.
- **Works with a weak connection:** challenges can be completed offline and sync automatically when the phone reconnects.
- **Security:** passwords are hashed by Firebase Authentication and the app never stores them. Firestore rules only allow signed-in users to read and write. The Firebase `google-services.json` file is kept out of the repository.
- **Safe redemptions:** rewards are redeemed in a Firestore transaction, so a balance can never go negative, even with a double tap.
- **Backend change:** the Planning Document proposed a PHP/MySQL API. I switched to Firebase (Authentication and Cloud Firestore) to deliver the required features more reliably within the time available. The app also calls a live external REST API through Retrofit.

## Features in this version

| Feature | Details |
|---|---|
| Register and log in | Email and password through Firebase Authentication (encrypted by Firebase) |
| Single sign-on (SSO) | "Sign in with Google" through Firebase |
| Settings | Change password, switch language (English / isiZulu), log out |
| Multi-language | English and isiZulu using Android resource files and per-app locales |
| Challenges with offline mode and sync | Complete challenges and earn points, even without a connection; saved changes sync when the phone is back online |
| Rewards (user-defined feature) | Points balance, reward list from Firestore, redeem with a transaction, redemption history saved |
| REST API | Live ZAR exchange rates using Retrofit and Gson |
| Input validation | Empty or invalid input shows an error message and does not crash the app |
| Logging and comments | `Log` calls and code comments throughout the Kotlin files |

## Offline mode with sync

The Challenges screen uses Cloud Firestore's built-in local cache. When a user completes a challenge, a write batch records the completion and adds the points. If the phone is offline, Firestore saves the change on the device, the balance updates straight away, and the screen shows "Saved offline". When the connection returns, Firestore sends the queued change automatically and the screen shows "Synced with the server". Each user can complete a challenge once, using the record id `userId_challengeId`.

This uses Firestore's cache rather than a separate Room database.

## Release notes (changes since the prototype)

**Added in the final version**
- Google Sign-In (single sign-on) alongside email and password login.
- Full English and isiZulu support, with a language switcher in Settings.
- **Challenges screen with offline mode and sync** (innovative feature): complete challenges and earn points while offline, with a visible "Saved offline" and "Synced" status.
- **Rewards screen** (innovative feature): users get a starting balance of 100 points, can redeem rewards, see a "Not enough points" message, and each redemption is stored in Firestore.
- Firestore security rules changed from time-limited test mode to "signed-in users only".
- App tested on a physical Android device (Huawei Pura 80).

**Known limitations / future work**
- Push notifications: a Firebase Cloud Messaging topic subscription was attempted but did not work reliably on my test device, so I do not claim it as a working feature.
- Play Store preparation (signed release APK, store screenshots, final app icon) is not complete.
- Offline support covers the Challenges screen only; Rewards redemption needs a connection.

## GitHub and GitHub Actions

- Source code is in this repository (no zip files) with regular commits throughout development.
- `.github/workflows/build.yml` runs automatically on every push and pull request to `main`. It checks out the code, sets up JDK 17, recreates `google-services.json` from an encrypted GitHub Secret (`GOOGLE_SERVICES_JSON`), runs the unit tests with `./gradlew test`, and builds the debug APK with `./gradlew assembleDebug`.
- Keeping the Firebase file in a GitHub Secret means the key is never committed to the repository, while the cloud build still works.
- The offline feature was built on a separate `offline-feature` branch and merged into `main` after testing.
- The final commit is tagged `final-poe`.

## Testing

Unit tests in `app/src/test` cover email and password validation and the points calculation. They run automatically in GitHub Actions. The offline feature was tested by hand on a phone: turn on airplane mode, complete a challenge, see "Saved offline", turn airplane mode off, see "Synced", and check the record in Firestore.

## Demo video

- Main demo https:https://youtube.com/shorts/o-3c7teQ6Rw?feature=share
- Google Sign-In https://youtube.com/shorts/JdyMk0_EtUE?feature=share

## Screenshots

<img width="1256" height="2760" alt="image" src="https://github.com/user-attachments/assets/33c45e2c-d9d6-4df6-aa36-71bc56fd5e38" />
<img width="582" height="1280" alt="image" src="https://github.com/user-attachments/assets/a188e571-f5e3-4df4-942c-794015efc6cf" />
<img width="582" height="1280" alt="image" src="https://github.com/user-attachments/assets/57edf658-84bd-41cf-9e1c-9fb2e71a81c3" />
<img width="582" height="1280" alt="image" src="https://github.com/user-attachments/assets/1bce0968-4232-4858-b52a-614347fba102" />
<img width="582" height="1280" alt="image" src="https://github.com/user-attachments/assets/9c620696-68a3-4f4b-85d5-f6a0675b7015" />
<img width="758" height="662" alt="image" src="https://github.com/user-attachments/assets/afbdb9bb-c324-41a1-be1c-942adfdc655b" />
<img width="1241" height="640" alt="image" src="https://github.com/user-attachments/assets/5c0fb70b-55bd-4387-b42d-1422f006bb63" />


## How to run

1. Clone the repository and open it in Android Studio.
2. Add your own `google-services.json` from a Firebase project (package `com.example.zenzeleni`) to the `app` folder.
3. Enable Email/Password and Google in Firebase Authentication, and add your SHA-1 fingerprint.
4. Create a Firestore database with `challenges` and `rewards` collections, then run the app.

## AI use disclosure

I used two AI tools in this assessment: Claude (Anthropic) for most of the work, and DeepSeek briefly near the end.

**Claude**
- Walked me through the setup step by step: the Android Studio project, Firebase, Gradle dependencies, GitHub and GitHub Actions. It was my first time using Firebase and Retrofit.
- Generated the Kotlin and XML code for the Login, Register, Home, Settings, Rewards and Challenges screens. I pasted this into Android Studio, ran it, and tested it on my phone.
- Helped me understand build errors, such as a missing `google-services.json` in GitHub Actions, an SDK version mismatch, and a layout file created in the wrong folder, and suggested fixes that I applied.
- Drafted the structure and wording of this README, which I reviewed and edited.

**My own work:** I decided to move from the planned PHP/MySQL backend to Firebase, chose the exchange-rate API, created the Firebase project, database records and GitHub repository, tested the features on a real device, and recorded the demo video.

**DeepSeek:** I used it late in development when I was short on time. Some of its changes broke the Settings screen, so I reverted the project to an earlier working commit (the broken version is kept on a backup branch). None of its changes are in the final version.

I ran and tested all the code in the final version.

## References

- Android Developers (2026) *Per-app language preferences*. Available at: https://developer.android.com/guide/topics/resources/app-languages (Accessed: 5 November 2026).
- Firebase (2026) *Authenticate with Google on Android*. Available at: https://firebase.google.com/docs/auth/android/google-signin (Accessed: 5 November 2026).
- Firebase (2026) *Access data offline (Cloud Firestore)*. Available at: https://firebase.google.com/docs/firestore/manage-data/enable-offline (Accessed: 5 November 2026).
- Firebase (2026) *Transactions and batched writes (Cloud Firestore)*. Available at: https://firebase.google.com/docs/firestore/manage-data/transactions (Accessed: 5 November 2026).
- Square (2026) *Retrofit*. Available at: https://square.github.io/retrofit/ (Accessed: 5 November 2026).
- ExchangeRate-API (2026) *Open access endpoint*. Available at: https://www.exchangerate-api.com/docs/free (Accessed: 5 November 2026).
