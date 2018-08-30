package com.zt.spark.file

case class DopVisitInfoDTO(
                            var `X-Device-Id`: String,
                            //dop_visit_location
                            var `X-Location`: String,
                            //dop_visit_ip
                            var `X-IP`: String,
                            //dop_visit_url
                            var `X-Current-Url`: String,
                            //dop_visit_referUrl
                            var `X-Source-Url`: String,
                            //dop_visit_userId
                            var `X-User-Id`: String,
                            //dop_visit_referChannelCode
                            var `X-Code`: String,
                            //dop_visit_appId
                            var `App-Id`: String,
                            //dop_visit_channelId
                            var `Channel-Id`: String,
                            //dop_visit_mobileOperator
                            var `Mobile-Operator`: String,
                            //dop_visit_appVersion
                            var `App-Version`: String,
                            //dop_visit_time
                            var CurrentTime: java.lang.Long = 0L,
                            //dop_visit_event
                            var Event: String,
                            //dop_visit_extraInfo
                            var ExtraInfo: String,
                            //dop_visit_pageSessionId
                            var PageSessionID: String,
                            //dop_visit_title
                            var PageTitle: String,
                            //dop_visit_visitSessionId
                            var AppSessionID: String,
                            //dop_visit_pageElement
                            var PageElement: String,
                            //dop_visit_network
                            var Network: String,
                            //dop_visit_taskId
                            var taskId: String,
                            //dop_visit_locationWithItude
                            var Location: String,


                            // 设备信息存储
                            var Type: String,
                            //dop_visit_terminalType
                            var DeviceType: String,
                            //dop_visit_deviceOS
                            var OperatingSystem: String,
                            //dop_visit_screenHigh
                            var ScreenHigh: String,
                            //dop_visit_screenWide
                            var ScreenWide: String,
                            //dop_visit_deviceBrand
                            var DeviceBrand: String,
                            //dop_visit_deviceNo
                            var DeviceItemNo: String,
                            //dop_visit_deviceManuFacturer
                            var DeviceManufacturer: String,
                            //dop_visit_browser
                            var Browser: String,
                            //dop_visit_language
                            var Language: String,
                            var SystemVersion: String,

                            var PageLoadedTime:java.lang.Long = 0L,
                            var PageStayTime:java.lang.Long = 0L,

                            // 格式字段
                            var `_id_`: String,
                            var `_op_`: String,
                            var `_ts_`: java.lang.Long = 0L,

                            // Compute result
                            var dop_visit_host: String = "",
                            var dop_visit_visitId: String = "",
                            var dop_visit_id: String = "",
                            var dop_visit_path: String = "",
                            var dop_visit_referName: String = "",
                            var dop_visit_referKeyword: String = "",

                            var dop_visit_quit: Integer = 0,

                            var visitSessionFlag: String = ""
                          ) extends Serializable


