import { Form, Formik } from "formik";
import { useState } from "react";
import { Button } from "react-bootstrap";
import Popup from "reactjs-popup";
import ButtonSubmit from "../forminputs/ButtonSubmit";
import InputField from "../forminputs/InputField";
import GuestLayout from "./GuestLayout";

export default function WelcomePopup() {
  const [open, setOpen] = useState(true);
  const closeModal = () => setOpen(false);

  return (
    <Popup
      open={open}
      position="right center"
      modal
      closeOnDocumentClick
      className="popup-content popup-overlay"
      onClose={closeModal}
    >
      <div className="card-body popup-body py-5" data-popup="tooltip">
        <div className="container">
          <div className="row py-4 px-5">
            <div className="col-6">
              <img
                src="/images/Castled-Logo.png"
                alt="Castled Logo"
                className="my-3"
              />
              <h2 className="header mb-3">Welcome to Castled</h2>
              <h3 className="description mb-3">
                Enter your email to get latest updates on Castled.
              </h3>
              <Formik
                initialValues={{
                  email: "",
                }}
                onSubmit={(values) => {
                  closeModal();
                  console.log(values);
                }}
              >
                {({ values, setFieldValue, setFieldTouched }) => (
                  <Form>
                    <InputField
                      type="email"
                      name="email"
                      title="Email"
                      placeholder="Your email goes here"
                    />
                    <ButtonSubmit className="form-control btn-lg py-2">
                      Enter
                    </ButtonSubmit>
                    <Button
                      className="form-control btn-lg my-2"
                      variant="outline-primary"
                      onClick={closeModal}
                    >
                      Skip
                    </Button>
                  </Form>
                )}
              </Formik>
            </div>
            <div className="col-6">
              <img
                src="https://doodleipsum.com/700/flat?i=b27c0a34a1444c205ec7c49a651d3046"
                alt="Home Alone by Gustavo Pedrosa"
                className="popup-image"
              />
            </div>
          </div>
        </div>
      </div>
    </Popup>
  );
}
