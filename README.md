# MiniTR

A tmux project manager, inspired by Tmuxinator, and written in Kotlin. It reads
user-specified project files and creates tmux sessions based on them.

Please note that this is a very early version of MiniTR. I was focused on
getting specific input files and command line arguments to work, and I'll
be writing tests for it very soon.

## Installation Instructions

Simply run `./gradlew install`. The executable script will be located in
`~/.local/bin`, so make sure that folder exists before running. To
uninstall, run `./gradlew uninstall`.

If you just want to build MiniTR, run `./gradlew jar`. The jar can be found
in `build/libs`.

## Run Instructions

After installing, you can run MiniTR with the `minitr` command.

The following commands create new MiniTR project files:

* `minitr new [projectname]` creates a new minitr project file with the path
    `~/config/minitr/[projectname].minitr`.
* `minitr new-local [projectname]` creates a new minitr project file in your
    current directory named `.minitr`.

The following commands are related to creating and attaching to MiniTR project
files. Specifying a project name will make MiniTR read
`~/config/minitr/[projectname].minitr`, and leaving out a project name will
cause it to read the file `.minitr` in your local directory.

* `minitr start [projectname]` creates a new tmux session based on the project
    file and attaches to it.
* `minitr start-bg [projectname]` creates a new tmux session based on the
    project file without attaching.
* `minitr attach [projectname]` attaches to the tmux session created by the
    minitr project
* `minitr debug [projectname]` works the same as start, except the tmux commands
    are printed to stdout instead

## MiniTR Project Files

The following is an example of a project file:

```
[settings]
name = minitr
root = ~/projects/minitr
tmux-command = tmux
tmux-flags =
start-window = vim

[commands]
start = ""
attach = ""
detatch = ""
stop = ""
pre-window = ""

[window/vim]
layout = main-vertical
panes = "vim"

[window/built]
layout = main-vertical
panes = "ant" "ant -e run-nobuild"

[window/git]
panes = "git status"

```

For the settings block:

* `name` is the name of the tmux project that will be created.
* `root` is the root directory of the project.
* `tmux-command` is the tmux command used by MiniTR.
* `tmux-flags` specifies flags that will be added to every tmux command.
* `start-window` specifies the window that will open upon.

For the window blocks:

* Note that each window block will start a new window with the specified name.
* The name of the window is specified after the slash starting the block.
* `layout` specifies the window's layout.
* `panes` specifies which commands to run in each pane.
