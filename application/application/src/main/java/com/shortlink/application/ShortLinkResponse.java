package com.shortlink.application;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ShortLinkResponse {
  private String shortLink;
  private String downloadLink;
}
