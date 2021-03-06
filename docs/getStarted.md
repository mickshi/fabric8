## Get Started

### Installation

First you need to download a distribution such as one of these:

<ul class="download-block">
  <li class="download-box">
    <a href="http://central.maven.org/maven2/io/fabric8/fabric8-karaf/1.1.0.Beta5/fabric8-karaf-1.1.0.Beta5.zip">fabric8-karaf-1.1.0.Beta5.zip</a>
  </li>
  <li class="download-box">
    <a href="http://central.maven.org/maven2/io/fabric8/runtime/fabric8-tomcat/1.1.0.Beta5/fabric8-tomcat-1.1.0.Beta5.zip">fabric8-tomcat-1.1.0.Beta5.zip</a>
  </li>
  <li class="download-box">
    <a href="http://central.maven.org/maven2/io/fabric8/runtime/fabric8-tomee/1.1.0.Beta5/fabric8-tomee-1.1.0.Beta5.zip">fabric8-tomee-1.1.0.Beta5.zip</a>
  </li>
</ul>

Note that the [Apache Tomcat](https://tomcat.apache.org/) and [Apache TomEE](http://tomee.apache.org/) distributions are still experimental; the [Apache Karaf](http://karaf.apache.org/) based distro is currently rock solid and complete ;)

Then unpack the tarball:

    cd ~/Downloads
    unzip fabric8-karaf-1.1.0.Beta5.tar.gz
    cd fabric8-karaf-1.1.0.Beta5

Or you could build the project with [maven](http://maven.apache.org/) via:

    mvn -DskipTests clean install -Pall
    cd fabric/fabric8-karaf/target
    unzip fabric8-karaf-1.1.0-SNAPSHOT.zip
    cd fabric8-karaf-1.1.0-SNAPSHOT

### Create a fabric

You may wish to edit the user/password in etc/users.properties

Now to create a fabric using the karaf distribution type:

    bin/fabric8

if you are using the Tomcat or TomEE distribution type:

    bin/fabric8 run

Once the container has started up, you have a working Fabric and you can connect to the console

### Use the console

Then open the [hawtio based](http://hawt.io/) console via: <a class="btn btn-primary" href="http://localhost:8181/">karaf web console localhost:8181</a> or <a class="btn btn-primary" href="http://localhost:8080/hawtio/">tomcat web console localhost:8080/hawtio/</a>

Then check out the documentation in the [embedded fabric8 wiki](http://localhost:8181/hawtio/index.html#/wiki/branch/1.0/view/fabric/profiles) to guide you through all the available [profiles](#/site/book/doc/index.md?chapter=profiles_md) you can create in a new container or add to an existing container.

Or check out:

 * [how to use the command shell](#/site/book/doc/index.md?chapter=agent_md)
 * [how to use git with fabric8](#/site/book/doc/index.md?chapter=git_md)

### View a demo

To help you get started you could watch one of the demos in the  <a class="btn" href="https://vimeo.com/album/2635012">JBoss Fuse and JBoss A-MQ demo album</a>

For example try this: <a class="btn" href="https://vimeo.com/80625940">JBoss Fuse 6.1 Demo</a>
