package com.xten.tide.runtime.runtime.messages.cluster

/**
  * Created with IntelliJ IDEA. 
  * User: kongqingyu
  * Date: 2017/7/22 
  */
class Member {

}

class MemberStatus {

}

object MemberStatus{

  case object Up extends MemberStatus
  case object Down extends MemberStatus
  case object Remove extends MemberStatus
  case object Init extends MemberStatus

}

