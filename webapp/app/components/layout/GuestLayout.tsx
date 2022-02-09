import Head from "next/head";
import Header from "@/app/components/layout/Header";
import React from "react";

interface GuestLayoutProps {
  title?: string;
  children: React.ReactNode;
}

const GuestLayout = ({ title, children }: GuestLayoutProps) => {
  return (
    <div className="bg-guest">
      {title ? (
        <Head>
          <title>{title ? title + " | " : ""} Castled</title>
        </Head>
      ) : null}
      <main className="w-100">
        <div className="card guest-card">
          <div className="card-body">{children}</div>
        </div>
      </main>
    </div>
  );
};
export default GuestLayout;
