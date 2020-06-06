/*
 *
 * Copyright 2020 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.spring.common.web;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.util.UriComponentsBuilder;

public final class Pageables {

  private Pageables() {}

  private static final Pageables INSTANCE = new Pageables();

  public static Pageables getInstance() {
    return INSTANCE;
  }

  public static String toQueryString(Pageable pageable,
      PageableParams pageableParams) {
    if (pageable == null) return "";

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/");
    uriBuilder.queryParam(pageableParams.getPageParameter(),
        String.valueOf(pageable.getPageNumber()));
    uriBuilder.queryParam(pageableParams.getSizeParameter(),
        String.valueOf(pageable.getPageSize()));
    pageable.getSort().forEach(order -> {
      uriBuilder.queryParam(pageableParams.getSortParameter(),
          order.getProperty() + "," + order.getDirection());
    });

    return uriBuilder.build().getQuery();
  }

  public static String toQueryString(Pageable pageable) {
    return toQueryString(pageable, PageableParams.ofSpring());
  }

  public static String toQueryStringWithoutPage(Pageable pageable,
      PageableParams pageableParams) {
    if (pageable == null) return "";

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/");
    uriBuilder.queryParam(pageableParams.getSizeParameter(),
        String.valueOf(pageable.getPageSize()));
    pageable.getSort().forEach(order -> {
      uriBuilder.queryParam(pageableParams.getSortParameter(),
          order.getProperty() + "," + order.getDirection());
    });

    return uriBuilder.build().getQuery();
  }

  public static String toQueryStringWithoutPage(Pageable pageable) {
    return toQueryStringWithoutPage(pageable, PageableParams.ofSpring());
  }

  public static List<String> sortToParam(Sort sort) {
    return sort.stream().map(o -> o.getProperty() + "," + o.getDirection())
        .collect(Collectors.toList());
  }

  public static Sort paramToSort(String... params) {
    if (params.length == 0) return Sort.unsorted();

    List<Order> orderList = new ArrayList<>();

    for (String param : params) {
      String[] propAndDerct = param.split(",");
      if (propAndDerct.length == 1) {
        orderList.add(Order.by(propAndDerct[0]));
      } else if (propAndDerct.length == 2) {
        Direction direction;
        try {
          direction = Direction.fromString(propAndDerct[1]);
          if (direction == Direction.ASC) {
            orderList.add(Order.asc(propAndDerct[0]));
          }
          if (direction == Direction.DESC) {
            orderList.add(Order.desc(propAndDerct[0]));
          }
        } catch (Exception e) {
          orderList.add(Order.by(propAndDerct[0]));
        }
      }
    }

    return Sort.by(orderList);
  }

  public static class PageableParams {

    public static PageableParams of(String pageParameter, String sizeParameter,
        String sortParameter) {
      return new PageableParams(pageParameter, sizeParameter, sortParameter);
    }

    public static PageableParams ofSpring() {
      return new PageableParams("page", "size", "sort");
    }

    private String pageParameter;
    private String sizeParameter;
    private String sortParameter;

    private PageableParams(String pageParameter, String sizeParameter,
        String sortParameter) {
      this.pageParameter = pageParameter;
      this.sizeParameter = sizeParameter;
      this.sortParameter = sortParameter;
    }

    public String getPageParameter() {
      return pageParameter;
    }

    public String getSizeParameter() {
      return sizeParameter;
    }

    public String getSortParameter() {
      return sortParameter;
    }

  }

}
