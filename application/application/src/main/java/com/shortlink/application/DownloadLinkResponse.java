package com.shortlink.application;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DownloadLinkResponse {
  private byte[] fileData;
}
