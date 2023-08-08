import Button from "@mui/material/Button";
import { FC } from "react";

import DownloadIcon from "@mui/icons-material/Download";

export enum DownloadType {
  CSV = "text/csv",
}

interface DownloadButtonProps {
  filename: string;
  label: string;
  data: Blob | undefined;
  type: DownloadType;
}

const BlobDownloadButton: FC<DownloadButtonProps> = ({
  filename,
  label,
  data,
  type,
}) => {
  const handleClick = () => {
    if (data) {
      const blob = new Blob([data], {
        type: type,
      });

      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.setAttribute("download", filename);
      document.body.appendChild(link);
      link.click();
      link.parentNode?.removeChild(link);
    }
  };

  return (
    <Button
      onClick={handleClick}
      endIcon={<DownloadIcon />}
      sx={{
        color: "highlights.main",
        marginX: "1rem",
      }}
    >
      {label}
    </Button>
  );
};

export default BlobDownloadButton;
