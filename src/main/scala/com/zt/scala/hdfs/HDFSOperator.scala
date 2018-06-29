package com.zt.scala.hdfs

import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

import scala.collection.mutable.ListBuffer

object HDFSOperator extends App {

  start(args)


  def start(args: Array[String]): Unit = {
    val conf = new Configuration()
    val uri = new URI("hdfs://192.168.129.101:9000")
    val hdfs = FileSystem.get(uri, conf)

    args(0) match {
      case "list" => traverse(hdfs, args(1))
      case "createFile" => HDFSHelper.createFile(hdfs, args(1))
      case "createFolder" => HDFSHelper.createFolder(hdfs, args(1))
      case "copyfile" => HDFSHelper.copyFile(hdfs, args(1), args(2))
      case "copyfolder" => HDFSHelper.copyFolder(hdfs, args(1), args(2))
      case "delete" => HDFSHelper.deleteFile(hdfs, args(1))
      case "copyfilefrom" => HDFSHelper.copyFileFromLocal(hdfs, args(1), args(2))
      case "copyfileto" => HDFSHelper.copyFileToLocal(hdfs, args(1), args(2))
      case "copyfolderfrom" => HDFSHelper.copyFolderFromLocal(hdfs, args(1), args(2))
      case "copyfolderto" => HDFSHelper.copyFolderToLocal(hdfs, args(1), args(2))
    }
  }

  def traverse(hdfs: FileSystem, hdfsPath: String) = {
    val holder: ListBuffer[String] = new ListBuffer[String]
    HDFSHelper.listChildren(hdfs, hdfsPath, holder)
    val paths: List[String] = holder.toList
    for (path <- paths) {
      System.out.println("--------- path = " + path)
      System.out.println("--------- Path.getname = " + new Path(path).getName)
    }
  }
}
