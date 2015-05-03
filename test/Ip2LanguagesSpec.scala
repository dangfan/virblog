import models.IP2Languages
import play.api.test._


class Ip2LanguagesSpec extends PlaySpecification {

  "Ip2Languages" should {

    "convert big ipv4 to BigDecimal" in new WithApplication {
      val result = IP2Languages.ipToDecimal("216.58.221.78")
      result must equalTo(BigDecimal("3627736398"))
    }

    "convert small ipv4 to BigDecimal" in new WithApplication {
      val result = IP2Languages.ipToDecimal("59.66.139.187")
      result must equalTo(BigDecimal("994216891"))
    }

    "convert ipv6 to BigDecimal" in new WithApplication {
      val result = IP2Languages.ipToDecimal("2001:da8:ff3a:c8eb:84a8:8c55:18f5:823b")
      result must equalTo(BigDecimal("42540765222621736648495700228902847035"))
    }

    "return en" in new WithApplication {
      val result = IP2Languages.getLangCode("216.58.221.78")
      await(result) must equalTo("en")
    }

    "return zh-CN (ipv4)" in new WithApplication {
      val result = IP2Languages.getLangCode("59.66.139.187")
      await(result) must equalTo("zh-CN")
    }

    "return zh-CN (ipv6)" in new WithApplication {
      val result = IP2Languages.getLangCode("2001:da8:ff3a:c8eb:84a8:8c55:18f5:823b")
      await(result) must equalTo("zh-CN")
    }
  }
}
