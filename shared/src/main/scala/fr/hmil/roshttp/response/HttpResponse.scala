package fr.hmil.roshttp.response

import fr.hmil.roshttp.util.HeaderMap


private[roshttp] trait HttpResponse {
    val statusCode: Int
    val headers: HeaderMap[String]
    val body: Any
}