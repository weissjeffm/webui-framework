# webui-framework is Deprecated
No more pull requests will be accepted.  If you absolutely must have
webui-framework exactly as-is, you will need to fork it to make changes.

It was created at a time when remote git repos were very expensive to create,
so we just threw everything into the only repo we had.  It no longer
makes any sense to keep all these unrelated components together.  So
we broke them apart into their own repositories.


* [testng-listeners](https://github.com/RedHatQE/testng-listeners)
* [ssh-tools](https://github.com/RedHatQE/ssh-tools)
* [assertions](https://github.com/RedHatQE/assertions)
* [bz-checker](https://github.com/RedHatQE/bz-checker)
* [bugzilla-testng](https://github.com/RedHatQE/bugzilla-testng)
* [verify-testng](https://github.com/RedHatQE/verify-testng)

And then there are some lower level libraries that the above libraries
depend on, that you probably don't need to include directly.  They
should get pulled in automatically as dependencies.

# How to consume these new libraries

You'll need a build tool that's based on maven, (or maven itself, but
I don't recommend it).  I recommend [Gradle](http://www.gradle.org/)
for any general purpose java (or other JVM) project.  If your project
uses Clojure at all, then I recommend [Leiningen](https://github.com/technomancy/leiningen/).

## Finding the libraries in the repository

The libraries are stored in the [Clojars](http://clojars.org)
repository, in the
[com.redhat.qe](https://clojars.org/groups/com.redhat.qe) group.  You
can click on any of those to find the latest released build.

## Setting up gradle

Add `http://clojars.org/repo` to the repositories section of your
`build.gradle` file, as has been done below.


```groovy
repositories {
    mavenCentral()
    [
        'https://repository.jboss.org/nexus/content/repositories/thirdparty-uploads',
        'http://clojars.org/repo',
        'http://download.java.net/maven/2/',
        'http://repository.codehaus.org',
        'http://snapshots.repository.codehaus.org'
    ].each { repo ->  
        maven {
            url repo
        }
    }
}
```

Then add the libraries you need to the `dependencies` section.

```groovy
dependencies {
    compile "com.redhat.qe:jul.test.records:1.0.1",
            "com.redhat.qe:assertions:1.0.2"
}
```

The format of specifying a dependency is slightly different between
leiningen and gradle. Clojars shows the leiningen way, but here's how
to translate:
* Leiningen:  `[group/libname "version.x.y.z"]`
* Gradle: `"group:libname:version.x.y.z"`

