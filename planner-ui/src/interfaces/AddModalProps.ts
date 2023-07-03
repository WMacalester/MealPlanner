export interface AddModalProps {
  open: boolean;
  handleClose: () => void;
}

export interface EditModalProps<T> extends AddModalProps {
  data: T;
}
