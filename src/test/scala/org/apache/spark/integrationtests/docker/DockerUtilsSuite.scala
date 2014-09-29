package org.apache.spark.integrationtests.docker

import org.apache.spark.integrationtests.docker.containers.spark.{SparkMaster, SparkWorker}
import org.scalatest.concurrent.Timeouts
import org.scalatest.{BeforeAndAfterEach, FunSuite, Matchers}

import org.apache.curator.framework.CuratorFramework
import org.apache.spark.SparkConf
import org.apache.spark.deploy.master.RecoveryState

class DockerUtilsSuite extends FunSuite with BeforeAndAfterEach with Matchers with Timeouts {

  override def afterEach(): Unit = {
    Docker.killAllLaunchedContainers()
  }

  test("basic container launching") {
    val container = Docker.launchContainer("ubuntu")
    assert(container.ip !== "")
    container.kill()
  }

  test("basic spark cluster") {
    val conf = new SparkConf()
    // Start a master
    val master = new SparkMaster(Seq.empty)
    master.waitForUI(10000)
    val masterState = master.getUpdatedState
    assert(masterState.numLiveApps === 0)
    assert(masterState.state === RecoveryState.ALIVE)
    assert(masterState.liveWorkerIPs.isEmpty)

    // Add a worker
    val worker = new SparkWorker(Seq.empty, master.masterUrl)
    worker.waitForUI(10000)
    master.getUpdatedState.liveWorkerIPs should be (Seq(worker.container.ip))

    worker.kill()
    master.kill()
  }

  test("basic zookeeper") {
    val zk = new ZooKeeperMaster()
    var client: CuratorFramework = null
    try {
      client = zk.newCuratorFramework()
      assert(client.getZookeeperClient.blockUntilConnectedOrTimedOut())
    } finally {
      client.close()
    }
  }
}