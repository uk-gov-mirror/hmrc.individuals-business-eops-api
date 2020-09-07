/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v1.controllers.requestParsers

import data.SubmitEndOfPeriodStatementData
import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v1.models.errors.{BadRequestError, ErrorWrapper, NinoFormatError, TypeOfBusinessFormatError}
import v1.models.requestData._
import play.api.mvc.AnyContentAsJson
import v1.mocks.MockSubmitEndOfPeriodStatementParser

class SubmitEndOfPeriodStatementParserSpec extends UnitSpec{
  val nino = "AA123456B"


  val inputData = SubmitEndOfPeriodStatementRawData(nino, AnyContentAsJson(SubmitEndOfPeriodStatementData.successJson))

  trait Test extends MockSubmitEndOfPeriodStatementParser {
    lazy val parser = new SubmitEndOfPeriodStatementParser(mockValidator)
  }

  "parse" should {

    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockValidator.validate(inputData).returns(Nil)

        parser.parseRequest(inputData) shouldBe
          Right(SubmitEndOfPeriodStatementRequest(Nino(nino),
            SubmitEndOfPeriodStatementData.validRequest))
      }
    }

    "return an ErrorWrapper" when {

      "a single validation error occurs" in new Test {
        MockValidator.validate(inputData).returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe Left(ErrorWrapper(None, Seq(NinoFormatError)))
      }

      "multiple validation errors occur" in new Test {
        MockValidator.validate(inputData).returns(List(NinoFormatError, TypeOfBusinessFormatError))

        parser.parseRequest(inputData) shouldBe Left(ErrorWrapper(None, Seq(BadRequestError, NinoFormatError, TypeOfBusinessFormatError)))
      }
    }
  }
}