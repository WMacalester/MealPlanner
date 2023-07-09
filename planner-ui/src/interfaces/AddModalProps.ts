export interface AddModalProps {
  open: boolean;
  handleClose: () => void;
}

export interface MutationModalProps<T> extends AddModalProps {
  data: T;
}
