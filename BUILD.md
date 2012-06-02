# How to Build

##Preparing the build with maven3

1. ``mvn release:prepare``

1. ``mvn clean deploy -DaltDeploymentRepository=snapshot-repo::default::file:build/snapshots/``

1. ``mvn clean deploy -DaltDeploymentRepository=release-repo::default::file:build/releases/``

**Note:** You can also just change the version by hand (not recommanded).

##Add to GITHub
* ``git add --all --verbose``
* ``git commit --all -m "{Release,Snapshot}-N.N" --verbose -C --no-status``
* ``git push --repo=origin --all -v``


##Update local repo with the pushed changes
* ``git fetch suiveg_upstream``
* ``git merge suiveg_upstream/master``

Congrats, your completely Done!

##Notes

Vi trenger et skript som gjør dette når vi skal ha nightly-builds. Er ikke vanskelig :-) Vi kan lage en pom.xml som kan kjøres med disse skriptene som bruker en Profil "build" f.eks (-Pbuild) ;

``mvn clean deploy ... -Pbuild``
