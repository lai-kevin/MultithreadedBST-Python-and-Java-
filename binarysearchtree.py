class Node(object):
    def __init__(self, element=None, parent=None):
        self.element = element
        self.parent = parent
        self.left = None
        self.right = None

    def get_element(self):
        return self.element

    def __str__(self):
        return str(self.element)

    def get_minimum(self, root):
        if root is None:
            return None
        if root.left is None:
            return root
        return self.get_minimum(root.left)

    def get_next_node_climb(self, start):
        if start.parent is None:
            return None
        parent = start.parent
        if (parent.left is not None) and (parent.left is start):
            return parent
        return self.get_next_node_climb(parent)

    def get_next_node(self):
        if self.right is not None:
            return self.get_minimum(self.right)
        return self.get_next_node_climb(self)

    def __iter__(self):
        self.next_node = self.get_minimum(self)
        return self

    def __next__(self):
        return_node = self.next_node
        if return_node is None:
            raise StopIteration
        self.next_node = self.next_node.get_next_node()
        return return_node.element


class BinarySearchTree(object):
    def __init__(self, name, root):
        self.name = name
        self.root = None
        self.size = 0

    def add_aux(self, r, elem, parent):
        if r is None:
            self.size += 1
            return Node(elem, parent)

        # if r.element is None:
        # self.size += 1
        # return r

        if elem < r.get_element():
            r.left = self.add_aux(r.left, elem, r)

        elif elem > r.get_element():
            r.right = self.add_aux(r.right, elem, r)

        return r

    def add(self, element):
        self.root = self.add_aux(self.root, element, None)

    def add_all(self, *values):
        for x in values:
            self.add(x)
        if self.root is None:
            self.root = Node()

    def print_node(self, node):
        if node is None:
            return ""
        if (node.left is None) and (node.right is None):
            result = "" + str(node.element)
            return result
        elif (node.left is None) and (node.right is not None):
            result = "" + str(node.element) + " R:(" + self.print_node(node.right) + ")"
            return result
        elif (node.left is not None) and (node.right is None):
            result = "" + str(node.element) + " L:(" + self.print_node(node.left) + ")"
            return result
        result = "" + str(node.element) + " L:(" + self.print_node(node.left) + ")" + " R:(" + self.print_node(
            node.right) + ")"
        return result

    def __str__(self):
        result = "[" + self.name + "] " + self.print_node(self.root)
        return result

    def get_minimum_2(self, root):
        if root is None:
            return None
        if root.left is None:
            return root
        return self.get_minimum_2(root.left)

    def __iter__(self):
        self.next_node = self.get_minimum_2(self.root)
        return self

    def __next__(self):
        return_node = self.next_node
        if return_node is None:
            raise StopIteration
        self.next_node = self.next_node.get_next_node()
        return return_node.element


class Merger(object):
    @staticmethod
    def merge(tree1, tree2):
        counter1 = 0
        counter2 = 0
        it1 = iter(tree1)
        it2 = iter(tree2)
        new1 = False
        new2 = False
        result_list = []
        while len(result_list) < tree1.size + tree2.size:
            if (new1 is False) and (counter1 < tree1.size):
                val1 = next(it1)
                counter1 += 1
                new1 = True

            if (new2 is False) and (counter2 < tree2.size):
                val2 = next(it2)
                counter2 += 1
                new2 = True

            if (new1 is False) and (new2 is True):
                result_list.append(val2)
                new2 = False
            elif (new1 is True) and (new2 is False):
                result_list.append(val1)
                new1 = False
            elif val1 < val2:
                result_list.append(val1)
                new1 = False
            elif val1 > val2:
                result_list.append(val2)
                new2 = False
            else:
                result_list.append(val1)
                new1 = False
        return result_list


