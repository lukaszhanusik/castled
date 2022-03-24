import React, { useEffect } from "react";
import { Form, Formik } from "formik";
import authService from "@/app/services/authService";
import InputField from "@/app/components/forminputs/InputField";
import { useSession } from "@/app/common/context/sessionContext";
import { LoggedInUserDto } from "@/app/common/dtos/LoggedInUserDto";
import { NextRouter, useRouter } from "next/router";
import GuestLayout from "@/app/components/layout/GuestLayout";
import ButtonSubmit from "@/app/components/forminputs/ButtonSubmit";
import * as Yup from "yup";
import bannerNotificationService from "@/app/services/bannerNotificationService";
import { GetServerSidePropsContext } from "next";
import { AxiosResponse } from "axios";

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

function ActivateUser(props: serverSideProps) {
  const { setUser } = useSession();
  const router = useRouter();
  const formSchema = Yup.object().shape({
    password: Yup.string().required("This field is required"),
    confirmPassword: Yup.string().when("password", {
      is: (val: string) => (val && val.length > 0 ? true : false),
      then: Yup.string().oneOf(
        [Yup.ref("password")],
        "Passwords need to match"
      ),
    }),
  });

  useEffect(() => {
    if (!router.isReady) return;

    if (router.query.failure_message) {
      bannerNotificationService.error(router.query.failure_message);
    }
  }, [router.isReady]);

  return (
    <GuestLayout>
      <div className="p-4">
        <div className="text-center">
          <img
            src="/images/Castled-Logo.png"
            alt="Castled Logo"
            className="mb-4 mt-3"
          />
        </div>
        <Formik
          initialValues={{
            firstName: "",
            lastName: "",
            password: "",
            confirmPassword: "",
          }}
          validationSchema={formSchema}
          validateOnChange={false}
          validateOnBlur={false}
          onSubmit={(values) => handleActivateUser(values, setUser, router!)}
        >
          {({ values, setFieldValue, setFieldTouched }) => (
            <Form>
              <div className="row row-cols-2">
                <InputField
                  type="string"
                  name="firstName"
                  title="First Name"
                  placeholder="first name"
                />
                <InputField
                  type="string"
                  name="lastName"
                  title="Last Name"
                  placeholder="last name"
                />
              </div>
              <InputField
                type="password"
                name="password"
                title="Password"
                placeholder="password"
              />
              <InputField
                type="password"
                name="confirmPassword"
                title="Confirm Password"
                placeholder="confirm password"
              />
              <ButtonSubmit className="form-control" />
            </Form>
          )}
        </Formik>
      </div>
    </GuestLayout>
  );
}

export interface ActivateUserForm {
  firstName: string;
  lastName: string;
  password: string;
  confirmPassword: string;
}

const handleActivateUser = async (
  registerForm: ActivateUserForm,
  setUser: (session: LoggedInUserDto | null) => void,
  router: NextRouter
) => {
  if (process.browser) {
    if (!router.query.token) {
      bannerNotificationService.error("Something went wrong!");
      return;
    }
    await authService
      .activateUser({
        token: router.query.token as string,
        firstName: registerForm.firstName,
        lastName: registerForm.lastName,
        password: registerForm.password,
      })
      .then((res: AxiosResponse<any>) => {
        redirectHome(setUser, router);
      });
  }
};

const redirectHome = async (setUser: any, router: any) => {
  const res = await authService.whoAmI();
  setUser(res.data);
  await router.push("/");
};

export default ActivateUser;
