package com.zt.scala.vertx

import java.lang

import com.typesafe.scalalogging.LazyLogging
import io.vertx.core.json.{JsonArray, JsonObject}
import io.vertx.core.{AsyncResult, Handler, Vertx, VertxOptions}
import io.vertx.redis.op.RangeLimitOptions
import io.vertx.redis.{RedisClient, RedisOptions}

import scala.collection.JavaConversions._
import scala.concurrent.{Future, Promise}

case class AsyncRedisTemplate(redisOptions: RedisOptions,
                              vertx: Vertx = VertxTemplate(new VertxOptions()).vertx) extends LazyLogging with Serializable {

  private val redisClient = RedisClient.create(vertx, redisOptions)
  logger.info(s"Vertx Redis Server Start at {}:{}", redisOptions.getHost, redisOptions.getPort)

  sys.addShutdownHook {
    redisClient.close(new Handler[AsyncResult[Void]]() {
      override def handle(e: AsyncResult[Void]): Unit = {}
    })
  }

  def select(dbindex: Int): Future[Void] = {
    val p = Promise[Void]
    redisClient.select(dbindex,
      new Handler[AsyncResult[String]]() {
        override def handle(e: AsyncResult[String]): Unit = {
          if (e.succeeded()) {
            p.success(null)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def get(key: String): Future[String] = {
    val p = Promise[String]
    redisClient.get(key,
      new Handler[AsyncResult[String]]() {
        override def handle(e: AsyncResult[String]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def getrange(key: String, start: Long, end: Long): Future[String] = {
    val p = Promise[String]
    redisClient.getrange(key, start, end,
      new Handler[AsyncResult[String]]() {
        override def handle(e: AsyncResult[String]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def getset(key: String, value: String): Future[String] = {
    val p = Promise[String]
    redisClient.getset(key, value,
      new Handler[AsyncResult[String]]() {
        override def handle(e: AsyncResult[String]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def getbit(key: String, offset: Long): Future[Long] = {
    val p = Promise[Long]
    redisClient.getbit(key, offset,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def mget(keys: List[String]): Future[List[String]] = {
    val p = Promise[List[String]]
    redisClient.mgetMany(keys,
      new Handler[AsyncResult[JsonArray]]() {
        override def handle(e: AsyncResult[JsonArray]): Unit = {
          if (e.succeeded()) {
            if (e.result() == null) {
              p.success(List())
            } else {
              p.success(e.result().map(_.toString).toList)
            }
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def set(key: String, value: String): Future[Void] = {
    val p = Promise[Void]
    redisClient.set(key, value,
      new Handler[AsyncResult[Void]]() {
        override def handle(e: AsyncResult[Void]): Unit = {
          if (e.succeeded()) {
            p.success(null)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def append(key: String, value: String): Future[Void] = {
    val p = Promise[Void]
    redisClient.append(key, value,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(null)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def setex(key: String, seconds: Long, value: String): Future[Boolean] = {
    val p = Promise[Boolean]
    redisClient.setex(key, seconds, value,
      new Handler[AsyncResult[String]]() {
        override def handle(e: AsyncResult[String]): Unit = {
          if (e.succeeded()) {
            p.success(e.result() == "OK")
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def psetex(key: String, millis: Long, value: String): Future[Boolean] = {
    val p = Promise[Boolean]
    redisClient.psetex(key, millis, value,
      new Handler[AsyncResult[Void]]() {
        override def handle(e: AsyncResult[Void]): Unit = {
          if (e.succeeded()) {
            p.success(true)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def setnx(key: String, value: String): Future[Boolean] = {
    val p = Promise[Boolean]
    redisClient.setnx(key, value,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result() == 1)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def msetnx(keyvals: Map[String, String]): Future[Boolean] = {
    val p = Promise[Boolean]
    val j = new JsonObject()
    keyvals.foreach(i => j.put(i._1, i._2))
    redisClient.msetnx(j,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result() == 1)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def setbit(key: String, offset: Long, bit: Int): Future[Long] = {
    val p = Promise[Long]
    redisClient.setbit(key, offset, bit,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def setrange(key: String, offset: Int, value: String): Future[Long] = {
    val p = Promise[Long]
    redisClient.setrange(key, offset, value,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def mset(keyvals: Map[String, String]): Future[Void] = {
    val p = Promise[Void]
    val j = new JsonObject()
    keyvals.foreach(i => j.put(i._1, i._2))
    redisClient.mset(j,
      new Handler[AsyncResult[String]]() {
        override def handle(e: AsyncResult[String]): Unit = {
          if (e.succeeded()) {
            p.success(null)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def incr(key: String): Future[Long] = {
    val p = Promise[Long]
    redisClient.incr(key,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def incrby(key: String, increment: Long): Future[Long] = {
    val p = Promise[Long]
    redisClient.incrby(key, increment,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def decr(key: String): Future[Long] = {
    val p = Promise[Long]
    redisClient.decr(key,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def decrby(key: String, decrement: Long): Future[Long] = {
    val p = Promise[Long]
    redisClient.decrby(key, decrement,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def strlen(key: String): Future[Long] = {
    val p = Promise[Long]
    redisClient.strlen(key,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def ttl(key: String): Future[Long] = {
    val p = Promise[Long]
    redisClient.ttl(key,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def del(key: String): Future[Long] = {
    val p = Promise[Long]
    redisClient.del(key,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def delMany(keys: List[String]): Future[Long] = {
    val p = Promise[Long]
    if (keys.isEmpty) {
      p.success(0)
    } else {
      redisClient.delMany(keys,
        new Handler[AsyncResult[lang.Long]]() {
          override def handle(e: AsyncResult[lang.Long]): Unit = {
            if (e.succeeded()) {
              p.success(e.result())
            } else {
              p.failure(e.cause())
            }
          }
        }
      )
    }
    p.future
  }

  def exists(key: String): Future[Boolean] = {
    val p = Promise[Boolean]
    redisClient.exists(key,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result() == 1)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def existsMany(keys: List[String]): Future[Boolean] = {
    val p = Promise[Boolean]
    if (keys.isEmpty) {
      p.success(false)
    } else {
      redisClient.existsMany(keys,
        new Handler[AsyncResult[lang.Long]]() {
          override def handle(e: AsyncResult[lang.Long]): Unit = {
            if (e.succeeded()) {
              p.success(e.result() == 0)
            } else {
              p.failure(e.cause())
            }
          }
        }
      )
    }
    p.future
  }

  def expire(key: String, seconds: Long): Future[Boolean] = {
    val p = Promise[Boolean]
    redisClient.expire(key, seconds,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result() == 1)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def hget(key: String, field: String): Future[String] = {
    val p = Promise[String]
    redisClient.hget(key, field,
      new Handler[AsyncResult[String]]() {
        override def handle(e: AsyncResult[String]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def hset(key: String, field: String, value: String): Future[Void] = {
    val p = Promise[Void]
    redisClient.hset(key, field, value,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(null)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def hmget(key: String, fields: List[String]): Future[List[String]] = {
    val p = Promise[List[String]]
    if (fields.isEmpty) {
      p.success(List())
    } else {
      redisClient.hmget(key, fields,
        new Handler[AsyncResult[JsonArray]]() {
          override def handle(e: AsyncResult[JsonArray]): Unit = {
            if (e.result() == null) {
              p.success(List())
            } else {
              p.success(e.result().map(_.toString).toList)
            }
          }
        }
      )
    }
    p.future
  }

  def hdel(key: String, field: String): Future[Long] = {
    val p = Promise[Long]
    redisClient.hdel(key, field,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def hlen(key: String): Future[Long] = {
    val p = Promise[Long]
    redisClient.hlen(key,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def hvals(key: String): Future[List[String]] = {
    val p = Promise[List[String]]
    redisClient.hvals(key,
      new Handler[AsyncResult[JsonArray]]() {
        override def handle(e: AsyncResult[JsonArray]): Unit = {
          if (e.succeeded()) {
            if (e.result() == null) {
              p.success(List())
            } else {
              p.success(e.result().map(_.toString).toList)
            }
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def hkeys(key: String): Future[List[String]] = {
    val p = Promise[List[String]]
    redisClient.hkeys(key,
      new Handler[AsyncResult[JsonArray]]() {
        override def handle(e: AsyncResult[JsonArray]): Unit = {
          if (e.succeeded()) {
            if (e.result() == null) {
              p.success(List())
            } else {
              p.success(e.result().map(_.toString).toList)
            }
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def hdelMany(key: String, fields: List[String]): Future[Long] = {
    val p = Promise[Long]
    if (fields.isEmpty) {
      p.success(0)
    } else {
      redisClient.hdelMany(key, fields,
        new Handler[AsyncResult[lang.Long]]() {
          override def handle(e: AsyncResult[lang.Long]): Unit = {
            if (e.succeeded()) {
              p.success(e.result())
            } else {
              p.failure(e.cause())
            }
          }
        }
      )
    }
    p.future
  }

  def hexists(key: String, field: String): Future[Boolean] = {
    val p = Promise[Boolean]
    redisClient.hexists(key, field,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result() == 1)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }


  def sadd(key: String, value: String): Future[Boolean] = {
    val p = Promise[Boolean]
    redisClient.sadd(key, value,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result() == 1)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def smembers(key: String): Future[List[String]] = {
    val p = Promise[List[String]]
    redisClient.smembers(key,
      new Handler[AsyncResult[JsonArray]]() {
        override def handle(e: AsyncResult[JsonArray]): Unit = {
          if (e.succeeded()) {
            if (e.result() == null) {
              p.success(List())
            } else {
              p.success(e.result().map(_.toString).toList)
            }
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def srem(key: String, member: String): Future[Long] = {
    val p = Promise[Long]
    redisClient.srem(key, member,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def zadd(key: String, score: Double, member: String): Future[Void] = {
    val p = Promise[Void]
    redisClient.zadd(key, score, member,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(null)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def zrangebyscore(key: String, min: Double, max: Double): Future[List[String]] = {
    val p = Promise[List[String]]
    redisClient.zrangebyscore(key, min + "", max + "", RangeLimitOptions.NONE,
      new Handler[AsyncResult[JsonArray]]() {
        override def handle(e: AsyncResult[JsonArray]): Unit = {
          if (e.succeeded()) {
            p.success(e.result().map(_.toString).toList)
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def zrem(key: String, member: String): Future[Long] = {
    val p = Promise[Long]
    redisClient.zrem(key, member,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def zremMany(key: String, members: List[String]): Future[Long] = {
    val p = Promise[Long]
    if (members.isEmpty) {
      p.success(0)
    } else {
      redisClient.zremMany(key, members,
        new Handler[AsyncResult[lang.Long]]() {
          override def handle(e: AsyncResult[lang.Long]): Unit = {
            if (e.succeeded()) {
              p.success(e.result())
            } else {
              p.failure(e.cause())
            }
          }
        }
      )
    }
    p.future
  }

  def zremrangebyscore(key: String, min: Double, max: Double): Future[Long] = {
    val p = Promise[Long]
    redisClient.zremrangebyscore(key, min + "", max + "",
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }

  def zremrangebyrank(key: String, start: Long, end: Long): Future[Long] = {
    val p = Promise[Long]
    redisClient.zremrangebyrank(key, start, end,
      new Handler[AsyncResult[lang.Long]]() {
        override def handle(e: AsyncResult[lang.Long]): Unit = {
          if (e.succeeded()) {
            p.success(e.result())
          } else {
            p.failure(e.cause())
          }
        }
      }
    )
    p.future
  }


}
