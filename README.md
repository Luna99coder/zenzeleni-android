# Zenzeleni

A community-driven loyalty and rewards mobile app built for the South African market, developed as part of the PROG6212/APPR6312 Portfolio of Evidence.

## About

Zenzeleni ("do it yourselves" / "do it together" in isiZulu) lets users complete challenges, earn points, and redeem rewards from partner merchants — combining gamified engagement, offline-friendly design, and multi-language support to serve South African users across all levels of digital literacy.

## Features Implemented (Part 2 Prototype)

- User registration and login with encrypted password storage (via Firebase Authentication)
- Settings screen: change password, log out, language selection (English / isiZulu)
- Live connection to a cloud database (Cloud Firestore) displaying real challenge data
- Live connection to an external RESTful API (via Retrofit) displaying real-time ZAR currency exchange rates
- Unit tests covering validation and points logic
- Automated build and test pipeline via GitHub Actions

## A Note on Backend Choice

The original Planning & Design Document specified a custom PHP REST API with a MySQL database. During development, the authentication and database backend was switched to Firebase (Authentication + Cloud Firestore) to deliver the required features — encrypted authentication, a live database connection, and (in the final PoE) real-time notifications — more reliably within the project timeline. This satisfies the brief's requirement to connect to "a REST API you create, or any storage mechanism that fits the idea you have, that exists on the internet." Retrofit was also added to consume a live external RESTful API, directly demonstrating the module's RESTful API and external library learning outcomes.

## Tech Stack

- Kotlin, Android (min SDK 24)
- Firebase Authentication
- Cloud Firestore
- Retrofit + Gson (external RESTful API integration)
- GitHub Actions for CI

## Demo Video

[Video link to be added]

## AI Use Disclosure

AI assistance (Claude, Anthropic) was used during this assessment in the following ways:

1. **Guided step-by-step setup**: I used AI to walk me through setting up the Android Studio project, connecting Firebase, and configuring Gradle dependencies, since this was my first time integrating Firebase into an Android app.
2. **Code generation for standard patterns**: AI generated boilerplate code for standard Android patterns (Activity classes, XML layouts, Firebase Authentication calls, Retrofit interfaces) based on my requirements. I reviewed, tested, and understood each piece before including it, and ran and debugged all code myself in Android Studio.
3. **Debugging assistance**: When build errors occurred (e.g. missing `google-services.json` in GitHub Actions, compileSdk version mismatches), AI helped me interpret the error messages and identify fixes.
4. **CI/CD setup**: AI helped draft the GitHub Actions workflow file and explained how to securely store the Firebase configuration as a GitHub Secret rather than committing it to the repository.

All code was run, tested, and verified working by me on an Android emulator before submission. I take full responsibility for understanding and being able to explain every part of this codebase.