package org.apache.spark.integrationtests.utils.spark

object RecoveryState extends Enumeration {
  type MasterState = Value

  val STANDBY, ALIVE, RECOVERING, COMPLETING_RECOVERY = Value
}