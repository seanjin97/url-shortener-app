import { Box, Flex, Text } from "@chakra-ui/react";

import ThemeToggle from "./ThemeToggle";

const Header = () => {
  return (
    <Flex as="header" width="full" align="center">
      <Box
        marginLeft="auto"
        display="flex"
        justifyContent="space-between"
        width="100%"
      >
        <Text>redirect-express</Text>
        <ThemeToggle />
      </Box>
    </Flex>
  );
};

export default Header;
