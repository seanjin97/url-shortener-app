import { Flex, Link, Text } from "@chakra-ui/react";

const Footer = () => {
  return (
    <Flex as="footer" width="full" justifyContent="center">
      <Text fontSize="sm" color="gray.500">
        {new Date().getFullYear()} -{" "}
        <Link
          href="https://seanjin.netlify.app/"
          isExternal
          rel="noopener noreferrer"
        >
          seanjin.netlify.app
        </Link>
      </Text>
    </Flex>
  );
};

export default Footer;
