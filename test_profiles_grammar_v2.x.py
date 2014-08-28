#!/usr/bin/python3
# -*- coding; utf-8 -*-


"""Test JSON profiles from the Daydreaming app to check for correctness and
consistency."""


from functools import partial
import json
import os
from pprint import pprint, pformat
import re
import sys
from textwrap import indent
import unittest


class ProfilesFileTestCase(unittest.TestCase):

    def setUp(self):
        self.f = open(filename, 'r')

    def tearDown(self):
        self.f.close()


class JSONTestCase(ProfilesFileTestCase):

    description = 'Checking the file is proper JSON, formatted as we expect'

    def test_is_json(self):
        try:
            json.load(self.f)
        except ValueError:
            self.fail('{} is not valid JSON!'.format(filename))

    def test_format(self):
        p = json.load(self.f)
        self.assertIn('profiles', p, "Couldn't find a 'profiles' list in "
                      "the root object!")
        self.assertIsInstance(p['profiles'], list,
                              "The 'profiles' property doesn't contain a "
                              "list of profiles!")


class LoadedProfilesTestCase(ProfilesFileTestCase):

    type_names = {int: 'an integer',
                  float: 'a float',
                  str: 'a string',
                  dict: 'a JSON object',
                  list: 'a list',
                  bool: 'a boolean'}
    error_base = "'{1}' (in {0}) "
    error_in = "{0} has no '{1}' property"
    error_type = "'{1}' (in {0}) is not {2}"
    error_list_type = "'{1}' (in {0}) contains items that are not {2}"
    error_empty_list = "'{1}' (in {0}) is an empty list"
    error_empty_object = "'{1}' (in {0}) is an empty object"

    def setUp(self):
        super(LoadedProfilesTestCase, self).setUp()
        self.profiles = json.load(self.f)['profiles']

    def _test_presence(self, container, container_name, attr_name_rec):
        if (not isinstance(attr_name_rec, list) and
                not isinstance(attr_name_rec, tuple)):
            attr_name_rec = [attr_name_rec]

        # Test for presence recursively
        c = container
        cn = container_name
        for attr_name in attr_name_rec:
            # Test presence
            error_msg = self.error_in.format(cn, attr_name)
            self.assertIn(attr_name, c, error_msg)
            oldc = c
            c = c[attr_name]
            oldcn = cn
            cn = attr_name + ' in {}'.format(cn)

        # Keep final attr_name, and use deep container and container_name
        return oldc, oldcn, attr_name

    def _test_presence_and_type(self, container, container_name,
                                attr_name_rec, attr_type, list_attr_type=None,
                                attr_tester=None, attr_tester_error=None,
                                str_regex=None, str_regex_error=None):
        # Test for presence
        container, container_name, attr_name = self._test_presence(
            container, container_name, attr_name_rec)

        # Test type
        error_msg = self.error_type.format(container_name, attr_name,
                                           self.type_names[attr_type])
        self.assertIsInstance(container[attr_name], attr_type, error_msg)

        # Further test attribute if asked to
        if attr_tester is not None:
            error_msg = (self.error_base.format(container_name, attr_name) +
                         attr_tester_error)
            self.assertTrue(attr_tester(container[attr_name]), error_msg)

        # Test string regex parameters are consistent
        if (attr_type != str and
                (str_regex is not None or str_regex_error is not None)):
            raise ValueError("Can't give a regex check if the checked object"
                             "is not a string.")

        # The, if necessary, test string regex
        if (str_regex is not None and
                re.search(str_regex, container[attr_name]) is None):
            self.fail(self.error_type.format(container_name, attr_name,
                                             str_regex_error))

        # Check list item types if we're a list
        if attr_type is list:
            error_msg = self.error_empty_list.format(container_name, attr_name)
            self.assertTrue(len(container[attr_name]), error_msg)

            if list_attr_type is not None:
                error_msg = self.error_list_type.format(
                    container_name, attr_name, self.type_names[list_attr_type])
                for item in container[attr_name]:
                    self.assertIsInstance(item, list_attr_type, error_msg)

        # Check for empty object if we're an object
        if attr_type is dict:
            error_msg = self.error_empty_object.format(container_name,
                                                       attr_name)
            self.assertTrue(len(container[attr_name]), error_msg)

    def _test_on_all_profiles(self, tester, filter_func=None):
        for i, p in enumerate(filter(filter_func, self.profiles)):
            pname = "profile #{} (id {})".format(i, p['id'])
            tester(p, pname)

    def _test_presence_and_type_on_all_profiles(
            self, attr_name_rec, attr_type, list_attr_type=None,
            attr_tester=None, attr_tester_error=None, filter_func=None):
        self._test_on_all_profiles(
            partial(self._test_presence_and_type, attr_name_rec=attr_name_rec,
                    attr_type=attr_type, list_attr_type=list_attr_type,
                    attr_tester=attr_tester,
                    attr_tester_error=attr_tester_error),
            filter_func)


class ProfilesCorrectnessTestCase(LoadedProfilesTestCase):

    description = ('Checking the profiles contain the right fields, '
                   'with right values')

    def test_exp_id(self):
        self._test_presence_and_type_on_all_profiles('exp_id', str)

    def test_id(self):
        self._test_presence_and_type_on_all_profiles('id', str)

    def test_n_results(self):
        self._test_presence_and_type_on_all_profiles('n_results', int)

    def test_vk_pem(self):
        self._test_presence_and_type_on_all_profiles('vk_pem', str)

    def test_profile_data(self):
        self._test_presence_and_type_on_all_profiles(
            'profile_data', dict,
            filter_func=lambda p: p['n_results'] != 0)

    def test_age(self):
        self._test_presence_and_type_on_all_profiles(
            ['profile_data', 'age'], str,
            filter_func=(lambda p: 'profile_data' in p
                         and len(p['profile_data']) != 0))

    def test_education(self):
        self._test_presence_and_type_on_all_profiles(
            ['profile_data', 'education'], str,
            filter_func=(lambda p: 'profile_data' in p
                         and len(p['profile_data']) != 0))

    def test_gender(self):
        self._test_presence_and_type_on_all_profiles(
            ['profile_data', 'gender'], str,
            filter_func=(lambda p: 'profile_data' in p
                         and len(p['profile_data']) != 0))

    def test_mode(self):
        self._test_presence_and_type_on_all_profiles(
            ['profile_data', 'mode'], str,
            filter_func=(lambda p: 'profile_data' in p
                         and len(p['profile_data']) != 0))

    def test_parametersVersion(self):
        self._test_presence_and_type_on_all_profiles(
            ['profile_data', 'parametersVersion'], str,
            filter_func=(lambda p: 'profile_data' in p
                         and len(p['profile_data']) != 0))

    def test_tipi_answers(self):
        self._test_presence_and_type_on_all_profiles(
            ['profile_data', 'tipi_answers'], dict,
            filter_func=(lambda p: 'profile_data' in p
                         and len(p['profile_data']) != 0))

    def test_nonzero_parametersVersion_if_nonzero_results(self):
        self._test_presence_and_type_on_all_profiles(
            ['profile_data', 'parametersVersion'], str,
            filter_func=lambda p: p['n_results'] != 0,
            attr_tester=lambda v: v is not None and v != '-1',
            attr_tester_error='is emtpy but profile has results')


class ProfilesConsistencyTestCase(LoadedProfilesTestCase):

    description = ('Checking the profiles are consistent between each '
                   'other')

    def _build_identity(self, p):
        if 'profile_data' not in p or len(p['profile_data']) == 0:
            # No profile data, skip
            return None, None
        data = p['profile_data']
        if 'tipi_answers' not in data or len(data['tipi_answers']) == 0:
            # No tipi answers, skip
            return None, None
        if 'parametersVersion' not in data or 'mode' not in data:
            # Profile from earlier versions of the app, skip
            return None, None

        keys = ['age', 'education', 'gender']
        identity = tuple(data[k] for k in keys)
        full_identity = {k: data[k] for k in keys}
        identity += tuple(v for k, v in sorted(data['tipi_answers'].items(),
                                               key=lambda i: i[0]))
        full_identity['tipi_answers'] = data['tipi_answers']
        return identity, full_identity

    def test_duplicate_profile_datas(self):
        # Build dict of identifying tuple -> list of (profile_id, mode) tuples
        dupmap = {}
        full_identities = {}
        for p in self.profiles:
            identity, full_identity = self._build_identity(p)
            if identity is None:
                # This profile is bad, and will be caught in the other
                # correctness tests
                continue

            profile_id = p['id']
            mode = p['profile_data']['mode']
            if identity in dupmap:
                dupmap[identity] += [(profile_id, mode)]
            else:
                dupmap[identity] = [(profile_id, mode)]
                full_identities[identity] = full_identity

        # Find all duplicated identities
        dupidentities = [ident for ident, plist in dupmap.items()
                         if len(plist) >= 2]
        # Find duplicated identities with mode == production
        dupidentities_prod = [ident for ident in dupidentities
                              if 'production' in [m for pid, m in
                                                  dupmap[ident]]]

        # Fail if we found any of those
        if len(dupidentities_prod) > 0:
            error_msg = 'Duplicate identities with production profiles:\n'
            for dupidentity_prod in dupidentities_prod:
                error_msg += '  Profiles\n' + \
                    indent(pformat(dupmap[dupidentity_prod]), '    ') + '\n'
                error_msg += '  Identity\n' + \
                    indent(pformat(full_identities[dupidentity_prod]),
                           '    ') + '\n'
            self.fail(error_msg)


class bcolors(object):

    bluHEADER = '\033[95m'
    OKBLUE = '\033[94m'
    OKGREEN = '\033[92m'
    WARNING = '\033[93m'
    FAIL = '\033[91m'
    ENDC = '\033[0m'


def strip_stacktrace(trace):
    trace = trace[trace.index('\n'):]
    trace = trace[trace.index('\n'):]
    trace = trace[trace.index('\n'):]
    return trace[trace.index(':') + 2:].rstrip('\n')


def green(s):
    return bcolors.OKGREEN + s + bcolors.ENDC


def red(s):
    return bcolors.FAIL + s + bcolors.ENDC


def blue(s):
    return bcolors.OKBLUE + s + bcolors.ENDC


if __name__ == '__main__':
    # Make sure we have a single argument
    if len(sys.argv) != 2:
        sys.exit('Usage: {} json-profiles-from-yelandur'.format(
            os.path.split(sys.argv[0])[1]))
    filename = sys.argv[1]
    basefilename = os.path.split(filename)[1]
    # Don't pass on the argument to unittest
    sys.argv = sys.argv[:1]

    test_cases = [JSONTestCase,
                  ProfilesCorrectnessTestCase,
                  ProfilesConsistencyTestCase]

    # Error flag
    error = False

    # Say hello
    hello = "Checking profiles in '{}' for correctness and consistency".format(
        basefilename)
    print()
    print(hello)
    print("-" * len(hello))
    print()

    for tc in test_cases:
        sys.stdout.write(tc.description + " ... ")
        suite = unittest.defaultTestLoader.loadTestsFromTestCase(tc)
        res = unittest.TestResult()
        suite.run(res)
        if len(res.errors) or len(res.failures):
            error = True
            sys.stdout.write(red('Arg, there was a problem') + '\r\n')
            print
            sys.stdout.write(blue("Here's all the information "
                                  "I've got for you:") + '\r\n')

            for k, e in enumerate(res.errors):
                print()
                print("Error #{}".format(k + 1))
                print("--------")
                pprint(e)

            for k, f in enumerate(res.failures):
                print()
                print("Failure #{}".format(k + 1))
                print("----------")
                print(strip_stacktrace(f[1]))

            break
        else:
            sys.stdout.write(green('Ok') + '\r\n')

    print
    if error:
        print()
        sys.stdout.write(red("*** There was an error.") + '\r\n')
        sys.stdout.write(red("*** This might signal a bug in the app, "
                             "you SHOULD find out.") + '\r\n')
        sys.stdout.write(red("*** Please note that this script stops when it "
                             "finds its first error.") + '\r\n')
        sys.stdout.write(red("*** So there might be more errors not yet "
                             "detected.") + '\r\n')
        sys.exit(1)
    else:
        print()
        sys.stdout.write(green("*** Profiles seem to be ok!") + '\r\n')
        sys.exit(0)
