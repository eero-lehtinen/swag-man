# Swag-man

## Description

The purpose of Swag-man was to imitate classic Pac-Man as closely as possible in Java and make it happen in about two weeks.
I recreated all the original game mechanics with the help of <a href="https://www.gamasutra.com/view/feature/132330/the_pacman_dossier.php?page=1">this Gamasutra article</a>.
One difference in my version is that the game doesn't end on level 256, I store the level number in a 32-bit integer :).
Also my version runs at 100 fps while the original runs at 60 fps, which makes some bugs like passing through ghosts less likely to happen.

As this code is from when I was 16 years old, it doesn't follow the best practices. E.g. it uses lots of circular dependencies and **_a lot_** of if-statements. But ignoring that the game itself works well, which is nice.

![Tux, the Linux mascot](/swag-man_screenshot.png)

## Try it out

Download the jar file from the [releases page](https://github.com/birusq/swag-man/releases/tag/v1.0).
