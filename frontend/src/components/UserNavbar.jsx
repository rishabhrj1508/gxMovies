/* eslint-disable no-unused-vars */
import {
  Disclosure,
  DisclosureButton,
  DisclosurePanel,
  Menu,
  MenuButton,
  MenuItem,
  MenuItems,
} from "@headlessui/react";
import { Bars3Icon, XMarkIcon } from "@heroicons/react/24/outline";
import React, { useEffect, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const UserNavbar = () => {
  const { logout } = useAuth();
  const username = localStorage.getItem("username");
  const [initials, setInitials] = useState("");

  // Get initials from full name

  const getInitials = (name) => {
    return name
      .split(" ")
      .map((word) => word[0])
      .join("");
  };

  useEffect(() => {
    setInitials(getInitials(username));
  }, [username]);

  const navigate = useNavigate();
  const location = useLocation();

  const navigation = [
    { name: "Home", href: "/user/home" },
    { name: "Library", href: "/user/library" },
    { name: "Favorites", href: "/user/favorites" },
    { name: "Cart", href: "/user/cart" },
    { name: "Orders", href: "/user/orders" },
  ];

  function classNames(...classes) {
    return classes.filter(Boolean).join(" ");
  }

  const getClassNames = (href) => {
    return location.pathname === href
      ? "bg-gray-900 text-white"
      : "text-gray-300 hover:bg-gray-700 hover:text-white";
  };

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  const handleProfile = () => {
    navigate(`/user/profile`);
  };

  return (
    <Disclosure as="nav" className="bg-gray-800 left-0 top-0 sticky z-50">
      <div className="mx-auto max-w-7xl px-2 sm:px-6 lg:px-8">
        <div className="relative flex h-16 items-center justify-between">
          <div className="absolute inset-y-0 left-0 flex items-center sm:hidden">
            <DisclosureButton className="group relative inline-flex items-center justify-center rounded-md p-2 text-gray-400 hover:bg-gray-700 hover:text-white focus:outline-none focus:ring-2 focus:ring-inset focus:ring-white">
              <span className="absolute -inset-0.5" />
              <span className="sr-only">Open main menu</span>
              <Bars3Icon
                aria-hidden="true"
                className="block size-6 group-data-[open]:hidden"
              />
              <XMarkIcon
                aria-hidden="true"
                className="hidden size-6 group-data-[open]:block"
              />
            </DisclosureButton>
          </div>
          <div className="flex flex-1 items-center justify-center sm:items-stretch sm:justify-start">
            <div className="flex shrink-0 items-center">
              <img
                alt="GxMovies"
                src="/images/image.png"
                className="h-12 w-auto cursor-pointer"
                onClick={() => navigate(`/user/home`)}
              />
            </div>
            <div className="hidden sm:ml-6 sm:block">
              <div className="flex space-x-4 mt-1">
                {navigation.map((item) => (
                  <Link
                    key={item.name}
                    to={item.href}
                    className={`rounded-md px-3 py-2 text-sm font-medium ${getClassNames(
                      item.href
                    )}`}
                  >
                    {item.name}
                  </Link>
                ))}
              </div>
            </div>
          </div>
          <div className="absolute inset-y-0 right-0 flex items-center pr-2 sm:static sm:inset-auto sm:ml-6 sm:pr-0">
            {/* profile menu */}
            <Menu as="div" className="relative ml-3">
              <div>
                <MenuButton className="relative flex items-center justify-center w-10 h-10 rounded-full bg-gray-600 text-white text-lg font-bold">
                  <span>{initials}</span>
                </MenuButton>
              </div>
              <MenuItems
                transition
                className="absolute right-0 z-10 mt-2 w-48 origin-top-right rounded-md bg-white py-1 shadow-lg ring-1 ring-black/5"
              >
                <div className="px-4 py-2 text-gray-700 font-medium text-sm">
                  Hi, {username}
                </div>
                <MenuItem>
                  <button
                    onClick={handleProfile}
                    className="block w-full text-left px-4 py-2 text-sm text-gray-700"
                  >
                    Your Profile
                  </button>
                </MenuItem>
                <MenuItem>
                  <button
                    onClick={handleLogout}
                    className="block px-4 py-2 text-sm text-gray-700"
                  >
                    Sign out
                  </button>
                </MenuItem>
              </MenuItems>
            </Menu>
          </div>
        </div>
      </div>

      <DisclosurePanel className="sm:hidden">
        <div className="space-y-1 px-2 pb-3 pt-2">
          {navigation.map((item) => (
            <DisclosureButton
              key={item.name}
              as="a"
              href={item.href}
              aria-current={item.current ? "page" : undefined}
              className={classNames(
                item.current
                  ? "bg-gray-900 text-white"
                  : "text-gray-300 hover:bg-gray-700 hover:text-white",
                "block rounded-md px-3 py-2 text-base font-medium"
              )}
            >
              {item.name}
            </DisclosureButton>
          ))}
        </div>
      </DisclosurePanel>
    </Disclosure>
  );
};

export default UserNavbar;
