package fr.hmil.roshttp


private[roshttp] trait HttpResponse {
    val statusCode: Int
    val headers: HeaderMap[String]
    val body: Any
}