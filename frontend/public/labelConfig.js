window.labelConfig = {
  //user registration page
  userRegistration: {
    title: "Register as a User",
    fullnamePlaceholder: "Enter your full name",
    agePlaceholder: "Enter your age",
    emailPlaceholder: "Enter your email",
    passwordPlaceholder: "Enter your password",
    confirmPasswordPlaceholder: "Confirm your password",
    registerButtonIfLoadingTrue: "Sending OTP...",
    registerButtonIfLoadingFalse: "Register",
    otpPlaceholder: "Enter OTP sent to your email",
    otpButtonIfLoadingTrue: "Validating OTP...",
    otpButtonIfLoadingFalse: "Validate OTP",
    errors: {
      fullnameError: "Full name is required.",
      fullnameInvalid: "Full name can only contain letters and spaces.",
      ageError: "Please enter a valid age between 14 and 110.",
      emailFormatError: "Please enter a valid email address.",
      emailEndsWithCheck: "@gmail.com",
      emailEndsWithError: "Email must end with '@gmail.com'.",
      passwordError:
        "Password must be at least 8 characters long and include at least one letter , one number and one speical character.",
      confirmPasswordError: "Password and Confirm Password do not match.",
    },
    buttonText: {
      cancelButton: "Cancel",
    },
  },

  //for both user and admin login pages
  login: {
    userLoginTitle: "Welcome Back!",
    adminLoginTitle: "Admin Login",
    emailInputLabel: "Email Address",
    emailPlaceholder: "Enter your email",
    passwordInputLabel: "Password",
    passwordPlaceholder: "Enter your password",

    errors: {
      emailFormatError: "Please enter a valid email address.",
      emailEndsWithCheck: "@gmail.com",
      emailEndsWithError: "Email must end with @gmail.com.",
      passwordError: "Password must be at least 6 characters long.",
      userpasswordError:
        "Password must be at least 8 characters long and include at least one letter , one number and one speical character.",
    },
    buttonText: {
      loginButton: "Login",
    },
  },

  //profile page
  profile: {
    title: "Your Profile",
    nameDisplayLabel: "Name",
    ageDisplayLabel: "Age",
    emailDisplayLabel: "Email",

    form: {
      nameLabel: "Name",
      ageLabel: "Age",
      emailLabel: "Email",
    },

    buttonText: {
      updateButton: "Update Profile",
      saveButton: "Save Changes",
      cancelButton: "Cancel",
    },
  },

  //user home page
  userhome: {
    title: "Welcome to GxMovies!",
    titleDescription: "Lights, Camera, Action! 🎬 Enjoy your movie adventure!",
    searchPlaceholder: "Search movies...",
    genreLabel: "All Genres",
    sortDefaultLabel: "Sort By",
    sortRatingLabel: "Rating - High to Low",
    sortPriceLabel: "Price - Low to High",
    noMoviesFoundText: "No movies found.",
    fetchMoviesError:
      "Sorry , We couln't load movies at the moment. Please try again later.",
  },

  //user Library Page
  library: {
    errors: {
      fetchMoviesError:
        "Sorry, we couldn't load your purchased movies. Please try again later.",
      addReviewError: "Review cannot be empty. Please share your thoughts!",
      noMoviesText:
        "You haven't purchased any movies yet. Start exploring and add your favorites to your collection!",
    },

    title: "Your Film Vault",
    titleSubpart: "Ready to Watch Now!",

    buttonText: {
      watchButton: "Watch Now",
      writeReviewButton: "Write Review",
      submitReviewButton: "Submit Review",
      cancelButton: "Cancel",
    },

    unavailableText: "Unavailable",

    reviewModalTitle: "Write a Review for",
    textAreaPlaceholder: "Share your thoughts on the movie...",
  },

  //user favorites page..
  favorites: {
    title: "Your Favorites",

    errors: {
      fetchFavoritesError: "Error fetching your favorite movies.",
      fetchPurchasedError: "Error fetching your purchased movies.",
      fetchCartStatusError: "Error checking if movie is in cart.",
      removeFavoritesError: "Error removing movie from favorites.",
      addtoCartError: "Error adding movie to cart.",
    },
    loadingText: "Loading your favorite movies...",
    noFavoritesText: "You don't have any favorite movies yet...",
    noFavoritesSubText: "Start adding your favorites now.",

    buttonText: {
      continueBrowsingButton: "Continue Browsing",
      removeButton: "Remove",
      watchButton: "Watch Now",
      alreadyInCartButton: "Already In Cart",
      addtoCartButton: "Add to Cart",
    },
  },

  //cart page
  cart: {
    title: "Your Cart",
    emptyCartText: "Your Cart is empty..",
    emptyCartSubText:
      "Looks like you haven't added any movies to your cart yet..",

    buttonText: {
      continueShoppingButton: "Continue Shopping",
      clearCartButton: "Clear Cart",
      proceedToCheckOutButton: "Proceed to Checkout",
    },

    orderSummaryText: "Order Summary",
    totalPriceText: "Total Price",
  },

  //order history page
  orders: {
    loadingText: "Loading your orders",
    title: "Your Movie Orders",
    subTitle: "Ready to watch? Jump right in! ",
    noOrdersText: "You have no orders yet!",

    errors: {
      fetchPurchaseError: "Failed to fetch purchases. Please try again later.",
      fetchPurchaseDetailError:
        "Failed to fetch purchase details. Please try again later.",
      downloadInvoiceError:
        "Failed to download the invoice. Please try again later.",
    },

    buttonText: {
      goToLibraryButton: "Go to Library",
      downloadInvoiceButton: "Download Invoice",
    },
  },

  //admin dashboard page
  adminDashboard: {
    summary: {
      totalUsers: "Total Users",
      totalMovies: "Total Movies",
      totalRevenue: "Total Revenue",
    },
    charts: {
      moviesByGenre: "Movies by Genre",
      revenueByGenre: "Revenue by Genre",
      topUsers: "Top 5 Users",
    },
    chartColors: ["#F44336", "#E91E63", "#9C27B0"],
  },

  //movie management page
  movieManagement: {
    title: "Movie Management",
    searchPlaceholder: "Search by title",
    filterGenrePlaceholder: "All Genres",
    filterStatusPlaceholder: "All Status",
    addMovieTooltip: "Add Movie",
    loadingText: "Loading Movies...",

    tableHeaders: {
      title: "Title",
      genre: "Genre",
      rating: "Rating",
      price: "Price",
      status: "Status",
      actions: "Actions",
    },
    statusLabels: {
      available: "AVAILABLE",
      unavailable: "UNAVAILABLE",
    },
    pagination: {
      previousLabel: "Previous",
      nextLabel: "Next",
      breakLabel: "...",
    },
    swal: {
      updateSuccess: "Movie updated successfully.",
      updateTitle: "Updated!",
      addSuccess: "Movie added successfully.",
      addTitle: "Added!",
    },
    buttonText: {
      editButton: "Edit",
    },
  },

  //review management page

  reviewManagement: {
    title: "Reported Reviews",
    searchMoviePlaceholder: "Search by movie title",
    searchUsernamePlaceholder: "Search by username",
    loadingText: "Loading Reviews...",
    noReportsText: "No reported reviews to display.",

    tableHeaders: {
      reviewId: "Review ID",
      username: "User Name",
      movieTitle: "Movie Title",
      reviewText: "Review Text",
      actions: "Actions",
    },

    buttonText: {
      deleteButton: "Delete",
    },

    pagination: {
      previousLabel: "Previous",
      nextLabel: "Next",
      breakLabel: "...",
    },

    swal: {
      deleteConfirmTitle: "Are you sure?",
      deleteConfirmText: "Do you really want to delete this review?",
      deleteConfirmIcon: "warning",
      deleteConfirmButtonText: "Yes, delete it!",
      deleteSuccessTitle: "Deleted!",
      deleteSuccessText: "Review deleted successfully.",
      deleteCancelledTitle: "Cancelled",
      deleteCancelledText: "The review is not deleted.",
    },
  },

  //user management page

  userManagement: {
    title: "User Management",
    searchPlaceholder: "Search by username",
    sortPlaceholder: "All Status",
    loadingText: "Loading Users...",
    noUsersText: "No Users to Display.",

    tableHeaders: {
      userId: "User ID",
      name: "Name",
      email: "Email",
      status: "Status",
      actions: "Actions",
    },

    buttonText: {
      blockButton: "Block",
      unblockButton: "Unblock",
    },

    statusLabels: {
      active: "ACTIVE",
      blocked: "BLOCKED",
    },

    pagination: {
      previousLabel: "Previous",
      nextLabel: "Next",
      breakLabel: "...",
    },

    swal: {
      successIcon: "success",
      blockConfirmTitle: "Are you sure?",
      blockConfirmText: "You really want to block this user?",
      blockConfirmIcon: "warning",
      blockConfirmButtonText: "Yes, Block User!",
      unblockConfirmTitle: "Are you sure?",
      unblockConfirmText: "You really want to unblock this user?",
      unblockConfirmIcon: "warning",
      unblockConfirmButtonText: "Yes, Unblock User!",
      blockSuccessTitle: "Blocked!",
      blockSuccessText: "User Blocked successfully.",
      unblockSuccessTitle: "Unblocked!",
      unblockSuccessText: "User Unblocked successfully.",
    },

    toastMessages: {
      fetchError: "Failed to fetch users!",
      unblockError: "Failed to unblock user.",
    },
  },
};
