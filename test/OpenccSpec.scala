import controllers.Utils
import org.specs2._


class OpenccSpec extends mutable.Specification {

  "Opencc" should {

    "convert 简体 to 簡體" in {
      Utils.zhs2Zht("简体") must equalTo("簡體")
    }

  }
}
