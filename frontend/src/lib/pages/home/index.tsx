import { Flex, Input, Text } from "@chakra-ui/react";
import { NextSeo } from "next-seo";

import Box from "../../components/motion/Box";

const Home = () => {
  return (
    <Flex
      direction="column"
      alignItems="center"
      justifyContent="center"
      minHeight="70vh"
      gap={4}
      mb={8}
      w="full"
    >
      <NextSeo title="Home" />
      <Box>
        <Box marginBottom={8} w={[200, 400, 500]}>
          <Text fontSize={["lg", "lg", "3xl"]}>
            redirect-express is a custom shortlink generator tool that shortens
            your links quickly.
          </Text>
        </Box>
        <Box>
          <Input variant="flushed" placeholder="Enter your link here!" />
        </Box>
      </Box>
    </Flex>
  );
};

export default Home;
