import React, { useEffect } from "react";
import { Form, Formik } from "formik";
import authService from "@/app/services/authService";
import InputField from "@/app/components/forminputs/InputField";
import InputSelect from "@/app/components/forminputs/InputSelect";
import { useSession } from "@/app/common/context/sessionContext";
import renderUtils from "@/app/common/utils/renderUtils";
import { LoggedInUserDto } from "@/app/common/dtos/LoggedInUserDto";
import { AppCluster, AppClusterLabel } from "@/app/common/enums/AppCluster";
import { NextRouter, useRouter } from "next/router";
import GuestLayout from "@/app/components/layout/GuestLayout";
import ButtonSubmit from "@/app/components/forminputs/ButtonSubmit";
import * as yup from "yup";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { GetServerSidePropsContext } from "next";
import { ClusterLocationUrl } from "@/app/common/enums/ClusterLocation";

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

function Register(props: serverSideProps) {
  const { setUser } = useSession();
  const router = useRouter();
  const formSchema = yup.object().shape({
    firstName: yup.string().required("First Name is required"),
    password: yup.string().required("Password is required"),
    confirmPassword: yup.string().when("password", {
      is: (val: string) => (val && val.length > 0 ? true : false),
      then: yup
        .string()
        .required("Confirm Password is required")
        .oneOf([yup.ref("password")], "Passwords need to match"),
    }),
  });
  return (
    <GuestLayout>
      <Formik
        initialValues={{
          firstName: "",
          lastName: "",
          password: "",
          confirmPassword: "",
          clusterLocation: undefined,
        }}
        validationSchema={formSchema}
        onSubmit={(values) => handleRegisterUser(values, setUser, router!)}
      >
        {({ values, setFieldValue, setFieldTouched }) => (
          <Form>
            <div className="row row-cols-2">
              <InputField
                type="string"
                name="firstName"
                title="First Name"
                placeholder="Enter first name"
              />
              <InputField
                type="string"
                name="lastName"
                title="Last Name"
                placeholder="Enter last name"
              />
            </div>
            <InputField
              type="password"
              name="password"
              title="Password"
              placeholder="Enter password"
            />
            <InputField
              type="password"
              name="confirmPassword"
              title="Confirm Password"
              placeholder="Confirm password"
            />
            <InputSelect
              title="Cluster Region"
              options={renderUtils.selectOptions(AppClusterLabel)}
              values={values}
              setFieldValue={setFieldValue}
              setFieldTouched={setFieldTouched}
              name="clusterLocation"
            />
            <p className="text-muted mt-n3">
              Choose a region nearest to your warehouse region
            </p>
            <ButtonSubmit className="form-control btn-lg">
              Register
            </ButtonSubmit>
            <p className="mt-3 fs-4 text-muted">
              By clicking on <strong className="text-muted">Register</strong>{" "}
              you agree to the{" "}
              <a href="https://castled.io/terms-of-service" target="_blank">
                Terms of Service
              </a>{" "}
              and the{" "}
              <a href="https://castled.io/privacy-policy" target="_blank">
                Privacy Policy
              </a>
            </p>
          </Form>
        )}
      </Formik>
      {/* <div className="mt-3 text-center">
        <Button
          href={authUtils.getExternalLoginUrl(
            props.appBaseUrl,
            ExternalLoginType.GOOGLE,
            router.pathname
          )}
          onClick={buttonHandler({ id: "signup_with_google" })}
        >
          Sign up with Google
        </Button>
      </div> */}
    </GuestLayout>
  );
}

export interface RegisterForm {
  firstName: string;
  lastName: string;
  password: string;
  confirmPassword: string;
  clusterLocation: AppCluster | undefined;
}

const handleRegisterUser = async (
  registerForm: RegisterForm,
  setUser: (session: LoggedInUserDto | null) => void,
  router: NextRouter
) => {
  if (process.browser) {
    let formData = {
      token: router.query.token as string,
      firstName: registerForm.firstName,
      lastName: registerForm.lastName,
      password: registerForm.password,
      clusterLocation: registerForm.clusterLocation,
    };
    if (formData.clusterLocation === undefined) {
      return bannerNotificationService.error("Please Select Cluster Location");
    }
    registerUser(
      formData,
      ClusterLocationUrl[formData.clusterLocation] +
        "/backend/v1/users/register"
    ).then(async (result) => {
      window.location.assign(ClusterLocationUrl[formData.clusterLocation!]);
    });
  }
};

const registerUser = async (formData: any, url: string) => {
  return fetch(url, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    credentials: "include",
    body: JSON.stringify(formData),
  });
};

export default Register;
