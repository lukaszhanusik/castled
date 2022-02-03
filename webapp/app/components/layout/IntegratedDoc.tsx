import { ConnectorCategory } from "@/app/common/utils/types";
import documentationService from "@/app/services/documentationService";
import React from "react";
import { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";

export interface IntegratedDocProps {
  category: ConnectorCategory;
  connectorType: string;
}

const IntegratedDoc = ({ category, connectorType }: IntegratedDocProps) => {
  const [markdown, setMarkdown] = useState<string>("");
  const [markdownNotFound, setMarkdownNotFound] = useState<boolean>(false);

  let docImgUrl =
    process.env.DOC_APP_IMAGE_BASE_URL ||
    "https://raw.githubusercontent.com/castledio/castled/main/docs/static/"; // NP: This can be deleted and url can be loaded only from env.

  useEffect(() => {
    documentationService
      .load(category, connectorType)
      .then((markdown) => {
        console.log(markdown);
        const metaPos = markdown.lastIndexOf("\n---\n");
        setMarkdownNotFound(markdown == "404: Not Found");
        setMarkdown(
          metaPos === -1 ? markdown : markdown.substring(metaPos + 5)
        );
      })
      .catch((error) => {
        console.log(error);
      });
  }, [category, connectorType]);
  return (
    <div>
      {markdownNotFound ? (
        <div className="doc-not-found">
          <p>
            We are working on the documentation of this {category}. If you face
            any issues during the setup, please contact support on chat.
          </p>
        </div>
      ) : (
        <ReactMarkdown
          className="integrated-doc"
          transformImageUri={(uri) =>
            uri.startsWith("http") ? uri : `${docImgUrl}${uri}`
          }
        >
          {markdown}
        </ReactMarkdown>
      )}
    </div>
  );
};

export default IntegratedDoc;
