import React, { useEffect } from "react";
import { Form, Formik } from "formik";
import InputField from "@/app/components/forminputs/InputField";
import { NextRouter, useRouter } from "next/router";
import GuestLayout from "@/app/components/layout/GuestLayout";
import ButtonSubmit from "@/app/components/forminputs/ButtonSubmit";
import * as yup from "yup";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { GetServerSidePropsContext } from "next";
import authService from "@/app/services/authService";
import { SignUpRequestDto } from "@/app/common/dtos/SignUpRequestDto";
import formHandler from "@/app/common/utils/formHandler";
import httpUtils from "@/app/common/utils/httpUtils";

export async function getServerSideProps(context: GetServerSidePropsContext) {
  return {
    props: {
      appBaseUrl: process.env.APP_BASE_URL,
    },
  };
}

interface serverSideProps {
  appBaseUrl: string;
  apiBase: string;
}

function SignUp(props: serverSideProps) {
  const router = useRouter();
  const formSchema = yup.object().shape({
    email: yup.string().email("Invalid email").required("Required"),
  });
  return (
    <div className="bg-guest">
      <main className="w-100">
        <div className="p-5 card guest-card" style={{ maxWidth: "50rem" }}>
          <div className="row py-2">
            <div className="col-6">
              <div className="text-center">
                <img
                  src="/images/Castled-Logo.png"
                  alt="Castled Logo"
                  className="mb-4 mt-3"
                />
              </div>
              <h2 className="mb-3">Create your Account</h2>
              <Formik
                initialValues={
                  {
                    email: router.query.email || "",
                  } as SignUpRequestDto
                }
                validationSchema={formSchema}
                validateOnChange={false}
                validateOnBlur={false}
                onSubmit={(values) =>
                  authService
                    .signUp(values)
                    .then(() => handleSignUp(values, router))
                }
              >
                {({ values, setFieldValue, setFieldTouched }) => (
                  <Form>
                    <InputField
                      type="email"
                      name="email"
                      title="Email"
                      placeholder="name@company.com"
                    />
                    <ButtonSubmit className="form-control btn-lg">
                      Sign Up
                    </ButtonSubmit>
                    <h4 className="mt-3">
                      <a href="/auth/login">
                        <span style={{ fontWeight: 400 }}>
                          Already have an account?
                        </span>{" "}
                        <span style={{ fontWeight: "bold" }}>Sign In</span>
                      </a>
                    </h4>
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
      </main>
    </div>
  );
}
const handleSignUp = async (values: SignUpRequestDto, router: NextRouter) => {
  if (process.browser) {
    await router.push(httpUtils.getUrl("/auth/verify-email", values));
  }
};

export default SignUp;
