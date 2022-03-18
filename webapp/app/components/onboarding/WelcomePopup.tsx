import { Form, Formik } from "formik";
import { useState } from "react";
import Popup from "reactjs-popup";
import ButtonSubmit from "../forminputs/ButtonSubmit";
import InputField from "../forminputs/InputField";
import authService from "@/app/services/authService";
import { handleLogin } from "@/pages/auth/login";
import { useSession } from "@/app/common/context/sessionContext";
import { useRouter } from "next/router";

export default function WelcomePopup() {
  const { setUser } = useSession();
  const router = useRouter();

  const [open, setOpen] = useState(true);
  const closeModal = () => setOpen(false);

  return (
    // <Popup
    //   open={open}
    //   position="right center"
    //   modal
    //   closeOnDocumentClick
    //   className="popup-content popup-overlay"
    //   onClose={closeModal}
    // >
    <div
      className="card-body welcome-popup popup-body py-5"
      data-popup="tooltip"
    >
      <div className="container">
        <div className="row pt-3 px-5">
          <div className="col-6">
            <img
              src="/images/Castled-Logo.png"
              alt="Castled Logo"
              className="my-3"
            />
            <h2 className="header mb-3">Thank you for choosing us.</h2>
            <h3 className="description mb-3">
              Enter your email to get started.
            </h3>
            <Formik
              initialValues={{
                email: "",
              }}
              onSubmit={(values) => {
                authService
                  .registerEmail(values)
                  .then((data) => {
                    handleLogin(setUser, router);
                    console.log(data);
                  })
                  .catch((error) => {
                    console.log(error);
                  });
                closeModal();
              }}
            >
              {({ values, setFieldValue, setFieldTouched }) => (
                <Form>
                  <InputField
                    type="email"
                    name="email"
                    title=""
                    placeholder="Your email goes here"
                  />
                  <ButtonSubmit className="form-control btn-lg py-2">
                    Get Started
                  </ButtonSubmit>
                </Form>
              )}
            </Formik>
          </div>
          <div className="col-6">
            <img
              src="/images/welcome-popup.svg"
              alt="Home Alone by Gustavo Pedrosa"
              className="popup-image"
            />
          </div>
        </div>
      </div>
    </div>
    // </Popup>
  );
}
