package org.splink.raven

import play.api.http.{ContentTypeOf, ContentTypes, Writeable}
import play.api.mvc.{Codec, Cookie, Result}

trait ResultOperations {
  implicit def resultOps(result: Result): ResultOps

  trait ResultOps {
    def withJavascript(js: Javascript*): Result
    def withJavascriptTop(js: Javascript*): Result
    def withCss(css: Css*): Result
    def withMetaTags(tags: MetaTag*): Result
  }
}

trait ResultImpl extends ResultOperations {
  self: Serializer =>

  override implicit def resultOps(result: Result): ResultOps = new ResultOpsImpl(result)

  class ResultOpsImpl(result: Result) extends ResultOps {
    override def withJavascript(js: Javascript*) = helper(js.map(_.src), Javascript.name)

    override def withJavascriptTop(js: Javascript*) = helper(js.map(_.src), Javascript.nameTop)

    override def withCss(css: Css*) = helper(css.map(_.src), Css.name)

    //TODO is serialization the correct approach?
    override def withMetaTags(tags: MetaTag*) =
      result.withHeaders(MetaTag.name -> tags.map(serializer.serialize).mkString("\n"))

    private def helper(elems: Seq[String], id: String) =
      result.withHeaders(s"$id" -> elems.mkString(","))
  }

  implicit val ct: ContentTypeOf[BrickResult] =
    ContentTypeOf[BrickResult](Some(ContentTypes.HTML))

  implicit def writeableOf(implicit codec: Codec, ct: ContentTypeOf[BrickResult]): Writeable[BrickResult] =
    Writeable(result => codec.encode(result.body.trim))
}

object BrickResult {
  val empty = BrickResult("")
}

case class BrickResult(body: String,
                       js: Set[Javascript] = Set.empty,
                       jsTop: Set[Javascript] = Set.empty,
                       css: Set[Css] = Set.empty,
                       cookies: Seq[Cookie] = Seq.empty,
                       metaTags: Set[MetaTag] = Set.empty)
