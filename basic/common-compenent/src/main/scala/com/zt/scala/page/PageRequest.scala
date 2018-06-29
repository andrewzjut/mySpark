package com.zt.scala.page

import org.springframework.data.domain.{Pageable, Sort}

/**
  * Created on 2018/4/13.
  *
  * @author 迹_Jason
  */
class PageRequest(private var page: Int, private var size: Int, private var sort: Sort) extends Pageable with Serializable {

  if (page < 1) throw new IllegalArgumentException("Page index must not be less than one!")

  if (size < 1) throw new IllegalArgumentException("Page size must not be less than one!")

  page = page - 1

  def this(page: Int, size: Int) = {
    this(page, size, null)
  }

  /*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Pageable#getPageSize()
	 */ override def getPageSize: Int = size

  /*
   * (non-Javadoc)
   * @see org.springframework.data.domain.Pageable#getPageNumber()
   */ override def getPageNumber: Int = page + 1

  /*
   * (non-Javadoc)
   * @see org.springframework.data.domain.Pageable#getOffset()
   */ override def getOffset: Int = page * size

  /*
   * (non-Javadoc)
   * @see org.springframework.data.domain.Pageable#hasPrevious()
   */ override def hasPrevious: Boolean = page > 0

  /*
   * (non-Javadoc)
   * @see org.springframework.data.domain.Pageable#previousOrFirst()
   */ override def previousOrFirst: Pageable = if (hasPrevious) previous
  else first

  override def getSort: Sort = sort

  /*
     * (non-Javadoc)
     * @see org.springframework.data.domain.Pageable#next()
     */ override def next = new PageRequest(getPageNumber + 1, getPageSize, getSort)

  /*
   * (non-Javadoc)
   * @see org.springframework.data.domain.AbstractPageRequest#previous()
   */ def previous: PageRequest = if (getPageNumber == 0) this
  else new PageRequest(getPageNumber - 1, getPageSize, getSort)

  /*
   * (non-Javadoc)
   * @see org.springframework.data.domain.Pageable#first()
   */ override def first = new PageRequest(1, getPageSize, getSort)

  /*
   * (non-Javadoc)
   * @see java.lang.Object#hashCode()
   */ override def hashCode: Int = {
    val prime = 31
    var result = 1
    result = prime * result + page
    result = prime * result + size
    result
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */ override def equals(obj: Any): Boolean = {
    if (this == obj) return true
    if (obj == null || (getClass ne obj.getClass)) return false
    val other = obj.asInstanceOf[PageRequest]
    this.page == other.page && this.size == other.size
  }
}
