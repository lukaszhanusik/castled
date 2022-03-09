import { Formik, Form } from "formik";
import { useState } from "react";
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
      className="popup-content"
      onClose={closeModal}
    >
      <GuestLayout>
        <div className="container">
          <div className="row py-2">
            <div className="col">
              <img
                src="/images/Castled-Logo.png"
                alt="Castled Logo"
                className="my-3"
              />
              <h2 className="mb-3">Create your Castled Account</h2>
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
                    <ButtonSubmit className="form-control btn-lg">
                      Enter
                    </ButtonSubmit>
                  </Form>
                )}
              </Formik>
            </div>
          </div>
        </div>
      </GuestLayout>
    </Popup>
  );
}
