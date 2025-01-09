# AstroidKush

This is a small desktop appliction I developed over christmas break as a test for implementing Lib Gdx game engine. It features some custom sprite art and an auto generative space background.


<img width="25%" alt = "8 Bit starship in black space during game screen" src="https://github.com/user-attachments/assets/c1122a98-411c-4976-9871-59856c91eb45" />


Notable challenges included getting the star sky to sit properly. In order to avoid running out of space on the original background I limited the height of the game world to 1000 pixels. Eventually I dropped the original background and opted for a generative star sky, considering the black background already present in the game engine. This made it so the running out of background map was no longer an issue. We still needed to reset the map size in order to avoid running out of integer memory. In order to reset every 1000 pixels we move everything on the game screen down 1000 pixels. This way we can theoretically keep playing indefinately (though current astroid randomisation makes this difficult) . 



<img width="25%"  alt ="Game Over Screen" src="https://github.com/user-attachments/assets/1bc0c0cb-a523-497e-98f5-6d0c3730cc8d"/>


 I wanted to channl my love for 80's retro games and styled the game accordingly. For pizel sprites i used a software called Aseprite - a pixel art / sprite creation software program with images made in adobe illustrator as referance images.


<img width="25%" alt= "8 Bit starship in black space during game screen"  src="https://github.com/user-attachments/assets/e604dc71-0442-4193-9ea1-fe619c84518b"  />




In the future I would love to keep developing this game, some improved versions may include music / sound design, improved movement animation - and generally better sprite tracking. Further features will include an option to shoot astroids and an actual score keeping system. Additionally I would want an opening menu screen and potentially an option for online multiplayer.

Gradle Info:

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
