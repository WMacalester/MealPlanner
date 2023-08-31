export const capitalise = (input: string) => {
  return input.replace(/(^\w{1})|(\s+\w{1})/g, (letter) =>
    letter.toUpperCase()
  );
};

export const isNameAlpha = (input: string) => {
  return RegExp(/^[A-Za-z ]*$/).test(input);
};

export const isNameAlphaNumeric = (input: string) => {
  return RegExp(/^[A-Za-z0-9 ]*$/).test(input);
};
