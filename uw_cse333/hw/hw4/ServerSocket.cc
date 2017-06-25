/*
 * Copyright 2012 Steven Gribble
 *
 *  This file is part of the UW CSE 333 course project sequence
 *  (333proj).
 *
 *  333proj is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  333proj is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with 333proj.  If not, see <http://www.gnu.org/licenses/>.
 */

#include <stdio.h>       // for snprintf()
#include <unistd.h>      // for close(), fcntl()
#include <sys/types.h>   // for socket(), getaddrinfo(), etc.
#include <sys/socket.h>  // for socket(), getaddrinfo(), etc.
#include <arpa/inet.h>   // for inet_ntop()
#include <netdb.h>       // for getaddrinfo()
#include <errno.h>       // for errno, used by strerror()
#include <string.h>      // for memset, strerror()
#include <iostream>      // for std::cerr, etc.

#include "./ServerSocket.h"

extern "C" {
  #include "libhw1/CSE333.h"
}

namespace hw4 {

ServerSocket::ServerSocket(uint16_t port) {
  port_ = port;
  listen_sock_fd_ = -1;
}

ServerSocket::~ServerSocket() {
  // Close the listening socket if it's not zero.  The rest of this
  // class will make sure to zero out the socket if it is closed
  // elsewhere.
  if (listen_sock_fd_ != -1)
    close(listen_sock_fd_);
  listen_sock_fd_ = -1;
}

bool ServerSocket::BindAndListen(int ai_family, int *listen_fd) {
  // Use "getaddrinfo," "socket," "bind," and "listen" to
  // create a listening socket on port port_.  Return the
  // listening socket through the output parameter "listen_fd".

  // MISSING:
  int serversock, retval;
  struct addrinfo hints, *results, *r;
  char portstr[10];

  snprintf(portstr, sizeof(portstr), "%hu", port_);

  memset(&hints, 0, sizeof(hints));
  hints.ai_family = ai_family;
  hints.ai_flags = AI_PASSIVE;
  hints.ai_socktype = SOCK_STREAM;
  
  if ((retval = getaddrinfo(NULL,
                            portstr,
                            &hints,
                            &results)) != 0)
  {
    std::cerr << "getaddrinfo failed: ";
    std::cerr << gai_strerror(retval) << std::endl;
    return false;
  }

  for (r = results; r != nullptr; r = r->ai_next) 
  {
    if ((serversock = socket(r->ai_family, SOCK_STREAM, 0)) == -1)
    {
      continue;
    }

    if (bind(serversock, r->ai_addr, r->ai_addrlen) == -1)
    {
      continue;
    }

    if (listen(serversock, 10) == -1)
    {
      continue;
    }
    listen_sock_fd_ = *listen_fd = serversock;
    freeaddrinfo(results);
    return true;
  }

  freeaddrinfo(results);
  return false;
}

bool ServerSocket::Accept(int *accepted_fd,
                          std::string *client_addr,
                          uint16_t *client_port,
                          std::string *client_dnsname,
                          std::string *server_addr,
                          std::string *server_dnsname) {
  // Accept a new connection on the listening socket listen_sock_fd_.
  // (Block until a new connection arrives.)  Return the newly accepted
  // socket, as well as information about both ends of the new connection,
  // through the various output parameters.

  // MISSING:

  struct sockaddr_storage addr;
  struct sockaddr* paddr = reinterpret_cast<struct sockaddr*>(&addr);
  char host[1024];
  char chost[1024];
  char service[1024];
  int retval;
  socklen_t addrlen = sizeof(addr);

  while (1)
  {
    retval = accept(listen_sock_fd_, paddr, &addrlen);
    if (retval < 0) {
      if (errno == EAGAIN || errno == EINTR)
        continue;

      return false;
    }
    break;
  }

  *accepted_fd = retval;

  if (paddr->sa_family == AF_INET)
  {
    char buf[INET_ADDRSTRLEN];
    struct sockaddr_in* psockaddr = reinterpret_cast<struct sockaddr_in*>(paddr);
    inet_ntop(AF_INET, &(psockaddr->sin_addr), buf, INET_ADDRSTRLEN);
    *client_addr = std::string(buf);
    *client_port = ntohs(psockaddr->sin_port);
  }
  else if (paddr->sa_family == AF_INET6)
  {
    char buf[INET6_ADDRSTRLEN];
    struct sockaddr_in6* psockaddr = reinterpret_cast<struct sockaddr_in6*>(paddr);
    inet_ntop(AF_INET6, &(psockaddr->sin6_addr), buf, INET6_ADDRSTRLEN);
    *client_addr = std::string(buf);
    *client_port = ntohs(psockaddr->sin6_port);
  }

  if (getnameinfo((struct sockaddr*) &addr, sizeof(addr),
                  chost, sizeof(chost), service, sizeof(service), 0) == 0)
  {
    *client_dnsname = std::string(chost);
  }

  host[0] = '\0';

  if (sock_family_ == AF_INET)
  {
    struct sockaddr_in serveraddr;
    socklen_t serverlen = sizeof(serveraddr);
    char buf[INET_ADDRSTRLEN];
    getsockname(retval, (struct sockaddr*) &serveraddr, &serverlen);
    inet_ntop(AF_INET, &serveraddr.sin_addr, buf, INET_ADDRSTRLEN);
    getnameinfo((const struct sockaddr*) &serveraddr, serverlen, host, sizeof(host), NULL, 0, 0);
    *server_addr = std::string(buf);
    *server_dnsname = std::string(host);
  }
  else
  {
    struct sockaddr_in6 serveraddr;
    socklen_t serverlen = sizeof(serveraddr);
    char buf[INET6_ADDRSTRLEN];
    getsockname(retval, (struct sockaddr*) &serveraddr, &serverlen);
    inet_ntop(AF_INET6, &serveraddr.sin6_addr, buf, INET6_ADDRSTRLEN);
    getnameinfo((const struct sockaddr*) &serveraddr, serverlen, host, sizeof(host), NULL, 0, 0);
    *server_addr = std::string(buf);
    *server_dnsname = std::string(host);
  }

  return true;
}

}  // namespace hw4
