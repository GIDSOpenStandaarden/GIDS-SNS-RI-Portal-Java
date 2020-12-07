/*
 * Copyright (c) 2020 Headease B.V., This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 */

const replaceKeywords = function (keywords, element) {
  for (const key in keywords) {
    if (keywords.hasOwnProperty(key)) {
      element.innerText = element.innerText.replace(`{${key}}`, keywords[key]);
    }
  }
};

const responseHandler = (response) => {
  if (response.status === 403) {
    window.location = '/'
  }
  return response.json();
}
