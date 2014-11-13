package controllers.changeKeeper

import controllers.BusinessKeeperDetails
import helpers.UnitSpec
import models.BusinessKeeperDetailsFormModel.Form.{FleetNumberId, BusinessNameId, EmailId, PostcodeId}
import pages.changekeeper.BusinessKeeperDetailsPage.{FleetNumberValid, BusinessNameValid, EmailValid, PostcodeValid}
import mappings.BusinessKeeperName

class BusinessKeeperDetailsFormSpec extends UnitSpec {

  "form" should {
    "accept if form is completed with all fields correct" in {
      val model = formWithValidDefaults().get
      model.fleetNumber should equal(Some(FleetNumberValid))
      model.businessName should equal(BusinessNameValid.toUpperCase)
      model.email should equal(Some(EmailValid))
    }

    "accept if form is completed with mandatory fields only" in {
      val model = formWithValidDefaults(
        fleetNumber = "",
        email = "").get
      model.fleetNumber should equal(None)
      model.businessName should equal(BusinessNameValid.toUpperCase)
      model.email should equal(None)
    }

    "reject if form has no fields completed" in {
      formWithValidDefaults(fleetNumber = "", businessName = "", email = "").
        errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.required", "error.validBusinessKeeperName")
    }
  }

  "businessName" should {
    "reject if business name is blank" in {
      // IMPORTANT: The messages being returned by the form validation are overridden by the Controller
      val errors = formWithValidDefaults(businessName = "").errors
      errors should have length 3
      errors(0).key should equal(BusinessNameId)
      errors(0).message should equal("error.minLength")
      errors(1).key should equal(BusinessNameId)
      errors(1).message should equal("error.required")
      errors(2).key should equal(BusinessNameId)
      errors(2).message should equal("error.validBusinessKeeperName")
    }

    "reject if business keeper name is less than minimum length" in {
      formWithValidDefaults(businessName = "A").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength")
    }

    "reject if business keeper name is more than the maximum length" in {
      formWithValidDefaults(businessName = "A" * BusinessKeeperName.MaxLength + 1)
        .errors.flatMap(_.messages) should contain theSameElementsAs List("error.maxLength")
    }
  }

  "postcode" should {
    "reject if postcode is empty" in {
      formWithValidDefaults(postcode = "M15A").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.restricted.validPostcode")
    }

    "reject if postcode is less than the minimum length" in {
      formWithValidDefaults(postcode = "M15A").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.minLength", "error.restricted.validPostcode")
    }

    "reject if postcode is more than the maximum length" in {
      formWithValidDefaults(postcode = "SA99 1DDD").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.maxLength", "error.restricted.validPostcode")
    }

    "reject if postcode contains special characters" in {
      formWithValidDefaults(postcode = "SA99 1D$").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validPostcode")
    }

    "reject if postcode contains an incorrect format" in {
      formWithValidDefaults(postcode = "SAR99").errors.flatMap(_.messages) should contain theSameElementsAs
        List("error.restricted.validPostcode")
    }

    "accept when a valid postcode is entered" in {
      val model = formWithValidDefaults(postcode = PostcodeValid).get
      model.postcode should equal(PostcodeValid)
    }
  }

  private def formWithValidDefaults(fleetNumber: String = FleetNumberValid,
                                    businessName: String = BusinessNameValid,
                                    email: String = EmailValid,
                                    postcode: String = PostcodeValid) = {
    injector.getInstance(classOf[BusinessKeeperDetails])
      .form.bind(
        Map(
          FleetNumberId -> fleetNumber,
          BusinessNameId -> businessName,
          EmailId -> email,
          PostcodeId -> postcode
        )
      )
  }
}