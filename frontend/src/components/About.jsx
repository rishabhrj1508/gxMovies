/* eslint-disable react/prop-types */
import { FaFilm, FaRocket, FaShieldAlt } from "react-icons/fa";

const AboutUs = () => {
  return (
    <>
      <div
        style={{
          background: "#fff",
          color: "#111",
          padding: "2rem 1rem",
          textAlign: "center",
          maxWidth: "900px",
          margin: "0 auto",
        }}
      >
        {/* Hero Section */}
        <h1
          style={{
            fontSize: "2rem",
            fontWeight: "bold",
            color: "#E50914",
            marginBottom: "1rem",
          }}
        >
          About Us 🎬
        </h1>
        <p style={{ fontSize: "1rem", color: "#555", marginBottom: "2rem" }}>
          Discover, purchase, and experience movies like never before with our
          seamless platform designed for movie lovers.
        </p>

        {/* Who We Are */}
        <h2
          style={{
            fontSize: "1.6rem",
            fontWeight: "600",
            marginBottom: "0.5rem",
          }}
        >
          Who We Are
        </h2>
        <p style={{ color: "#666", marginBottom: "1.5rem" }}>
          We provide a one-stop solution for discovering, purchasing, and
          enjoying movies securely.
        </p>

        {/* What We Offer */}
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            gap: "1rem",
            flexWrap: "wrap",
            marginBottom: "2rem",
          }}
        >
          <FeatureCard
            icon={<FaFilm />}
            title="Seamless Browsing"
            desc="Find and explore movies effortlessly."
          />
          <FeatureCard
            icon={<FaShieldAlt />}
            title="Secure Purchases"
            desc="Buy movies safely and quickly."
          />
          <FeatureCard
            icon={<FaRocket />}
            title="Fast & Reliable"
            desc="Experience a smooth, responsive platform."
          />
        </div>

        {/* Why Choose Us */}
        <h2
          style={{
            fontSize: "1.6rem",
            fontWeight: "600",
            marginBottom: "0.5rem",
          }}
        >
          Why Choose Us?
        </h2>
        <p style={{ color: "#666", marginBottom: "1.5rem" }}>
          A user-friendly, secure, and feature-rich platform designed for movie
          lovers.
        </p>

        {/* Our Mission */}
        <h2
          style={{
            fontSize: "1.6rem",
            fontWeight: "600",
            marginBottom: "0.5rem",
          }}
        >
          Our Mission 🚀
        </h2>
        <p style={{ color: "#666" }}>
          To revolutionize how people explore and experience movies through a
          seamless digital experience.
        </p>
      </div>
    </>
  );
};

// Feature Card Component
const FeatureCard = ({ icon, title, desc }) => {
  return (
    <div
      style={{
        background: "#f7f7f7",
        padding: "1rem",
        borderRadius: "8px",
        width: "250px",
        textAlign: "center",
        boxShadow: "0px 2px 8px rgba(0,0,0,0.1)",
      }}
    >
      <div
        style={{
          fontSize: "2rem",
          color: "#E50914",
          display: "flex",
          justifyContent: "center",
          marginBottom: "0.5rem",
        }}
      >
        {icon}
      </div>
      <h3
        style={{
          fontSize: "1.1rem",
          fontWeight: "600",
          marginBottom: "0.3rem",
        }}
      >
        {title}
      </h3>
      <p style={{ color: "#555", fontSize: "0.9rem" }}>{desc}</p>
    </div>
  );
};

export default AboutUs;
