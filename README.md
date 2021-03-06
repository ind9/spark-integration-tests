# Spark Integration Tests

This project contains [Docker](http://docker.com)-based integration tests for Spark, including fault-tolerance tests for Spark's standalone cluster manager.

## Installation / Setup

### Install Docker

This project depends on Docker >= 1.3.0 (it may work with earlier versions, but this hasn't been tested).

#### On Linux

Install Docker.  This test suite requires that Docker can run without `sudo` (see http://docs.docker.io/en/latest/use/basics/).

#### On OSX

On OSX, these integration tests can be run using [boot2docker](https://github.com/boot2docker/boot2docker).
First, [download `boot2docker`](https://github.com/boot2docker/osx-installer/releases/tag/v1.3.2), run the installer, then run `~/Applications/boot2docker` to perform some one-time setup (create the VM, etc.).  This project has been tested with `boot2docker` 1.3.0+.

With `boot2docker`, the Docker containers will be run inside of a VirtualBox VM, which creates some difficulties for communication between the Mac host and the containers.  Follow these instructions to work around those issues:
   
- **Network access**:  Our tests currently run the SparkContext from outside of the containers, so we need both host <-> container and container <-> container networking to work properly.  This is complicated by the fact that `boot2docker` runs the containers behind a NAT in VirtualBox.

  [One workaround](https://github.com/boot2docker/boot2docker/issues/528) is to add a routing table entry that routes traffic to containers to the VirtualBox VM's IP address:
  
  ```
  sudo route -n add 172.17.0.0/16 `boot2docker ip`    
  ```
  
  You'll have to re-run this command if you restart your computer or assign a new IP to the VirtualBox VM.
  
  
### Install Docker images

The integration tests depend on several Docker images.  To set them up, run

```
./docker/build.sh
```

to build our custom Docker images and download other images from the Docker repositories.  This needs to download a fair amount of stuff, so make sure that you're on a fast internet connection (or be prepared to wait a while).

### Configure your environment

**Quickstart**: Running `./init.sh` will perform environment sanity checking and tell you which shell exports to perform.

**Details**:

- The `SPARK_HOME` environment variable should to a Spark source checkout where an assembly has been built.  This directory will be shared with Docker containers; Spark workers and masters will use this `SPARK_HOME/work` as their work directory.  This effectively treats host machine's `SPARK_HOME` directory as a directory on a network-mounted filesystem.

  Additionally, this Spark sbt project will added as a dependency of this sbt project, so the integration test code will be compiled against that Spark version.


### Test-specific requirements

#### Mesos

The Mesos integration tests require `MESOS_NATIVE_LIBRARY` to be set.  For Mac users, the easiest way to install Mesos is through Homebrew:

```
brew install mesos
```

then

```
export MESOS_NATIVE_LIBRARY=$(brew --repository)/lib/libmesos.dylib
```

Spark on Mesos requires a Spark binary distribution `.tgz` file.  To build this, run `./make-distribution.sh --tgz` in your Spark checkout.

## Running the tests

These integration tests are implemented as ScalaTest suites and can be run through sbt.  Note that you will probably need to give sbt extra memory; with newer versions of the sbt launcher script, this can be done with the `-mem` option, e.g.

```
sbt -mem 2048 test:package "test-only org.apache.spark.integrationtests.MesosSuite"
```

*Note:* Although our Docker-based test suites attempt to clean up the containers that they create, this cleanup may not be performed if the test runner's JVM exits abruptly.  To kill **all** Docker containers (including ones that may not have been launched by our tests), you can run `docker kill $(docker ps -q)`.

## License

This project is licensed under the Apache 2.0 License. See LICENSE for full license text.
